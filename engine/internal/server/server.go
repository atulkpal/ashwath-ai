package server

import (
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net"
	"net/http"
	"os"
	"path/filepath"

	"github.com/ashwathai/ashwath-engine/internal/api"
	"github.com/ashwathai/ashwath-engine/internal/api/pb"
	"github.com/ashwathai/ashwath-engine/internal/config"
	"github.com/ashwathai/ashwath-engine/internal/device"
	"github.com/ashwathai/ashwath-engine/internal/downloads"
	"github.com/ashwathai/ashwath-engine/internal/logging"
	"github.com/ashwathai/ashwath-engine/internal/models"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"github.com/ashwathai/ashwath-engine/internal/runtime/llama"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/metadata"
	"google.golang.org/grpc/reflection"
	"google.golang.org/grpc/status"
	"google.golang.org/protobuf/encoding/protojson"
	"google.golang.org/protobuf/proto"
)

type Options struct {
	EngineType string
	LlamaBin   string
	ModelPath  string
}

type gatewayService struct {
	api    *api.EngineService
	log    logging.Logger
}

func Run(ctx context.Context, cfg *config.Config, opts Options) error {
	log := logging.New(os.Stdout, cfg.LogLevel)
	log.Info("Ashwath Engine starting",
		"version", "0.1.0",
		"port", cfg.Port,
		"data_dir", cfg.DataDir,
	)

	det := device.New()
	caps, err := det.Detect()
	if err != nil {
		log.Warn("device detection failed", "error", err)
	} else {
		log.Info("Device detected",
			"os", caps.OSType,
			"arch", caps.Arch,
			"cores", caps.CPUCores,
			"ram_gb", fmt.Sprintf("%.1f", caps.RAMGB),
		)
	}

	engType := opts.EngineType
	if engType == "" {
		engType = "llama"
	}
	log.Info("Engine type selected", "type", engType)

	downloader := downloads.NewManager()
	reg := models.NewRegistry(cfg.ModelsDir, downloader, models.WithOllamaSource())

	resolvedModelPath := opts.ModelPath
	if engType == "llama" && resolvedModelPath == "" {
		mdls, err := reg.List()
		if err != nil {
			return fmt.Errorf("list models: %w", err)
		}

		var installedModel *models.Model
		for _, m := range mdls {
			if m.Installed {
				installedModel = &m
				break
			}
		}

		if installedModel == nil && len(mdls) > 0 {
			m := mdls[0]
			log.Info("No installed model found, installing default", "model_id", m.ID)
			if err := reg.Install(m.ID); err != nil {
				return fmt.Errorf("install default model %s: %w", m.ID, err)
			}

			updated, err := reg.Get(m.ID)
			if err != nil {
				return fmt.Errorf("get installed model %s: %w", m.ID, err)
			}
			installedModel = updated
		}

		if installedModel != nil {
			resolvedModelPath = filepath.Join(cfg.ModelsDir, installedModel.ID, installedModel.Filename)
			log.Info("Using installed model", "path", resolvedModelPath)
		}
	}

	llama.Register()

	initOpts := runtime.Options{
		ModelPath:  resolvedModelPath,
		BinaryPath: opts.LlamaBin,
	}

	eng, err := runtime.CreateEngine(ctx, engType, initOpts)
	if err != nil {
		return fmt.Errorf("create engine: %w", err)
	}
	log.Info("Engine initialized", "name", eng.Name(), "type", engType)

	shutdownCh := make(chan struct{})
	api.SetShutdownChannel(shutdownCh)

	grpcServer := grpc.NewServer()
	svc := api.NewEngineService(eng, det, reg, log)
	pb.RegisterAshwathEngineServer(grpcServer, svc)
	reflection.Register(grpcServer)

	addr := fmt.Sprintf(":%d", cfg.Port)
	listener, err := net.Listen("tcp", addr)
	if err != nil {
		return fmt.Errorf("failed to listen on %s: %w", addr, err)
	}

	log.Info("gRPC server listening", "addr", listener.Addr().String())

	errCh := make(chan error, 1)
	go func() {
		if err := grpcServer.Serve(listener); err != nil {
			errCh <- fmt.Errorf("gRPC server error: %w", err)
		}
	}()

	httpAddr := fmt.Sprintf(":%d", cfg.Port+1)
	httpListener, err := net.Listen("tcp", httpAddr)
	if err != nil {
		log.Warn("HTTP gateway port unavailable, skipping", "addr", httpAddr)
	} else {
		gs := &gatewayService{api: svc, log: log}
		mux := http.NewServeMux()
		mux.HandleFunc("/status", gs.handleStatus)
		mux.HandleFunc("/health", gs.handleHealth)
		mux.HandleFunc("/version", gs.handleVersion)
		mux.HandleFunc("/ashwath.AshwathEngine/Generate", gs.handleGenerate)
		mux.HandleFunc("/ashwath.AshwathEngine/ListModels", gs.handleListModels)
		mux.HandleFunc("/ashwath.AshwathEngine/InstallModel", gs.handleInstallModel)
		mux.HandleFunc("/ashwath.AshwathEngine/RemoveModel", gs.handleRemoveModel)
		mux.HandleFunc("/ashwath.AshwathEngine/GetDeviceInfo", gs.handleGetDeviceInfo)
		mux.HandleFunc("/ashwath.AshwathEngine/Shutdown", gs.handleShutdown)

		httpServer := &http.Server{Handler: mux}
		go func() {
			log.Info("HTTP gateway listening", "addr", httpListener.Addr().String())
			if err := httpServer.Serve(httpListener); err != nil && err != http.ErrServerClosed {
				errCh <- fmt.Errorf("HTTP gateway error: %w", err)
			}
		}()
	}

	select {
	case <-ctx.Done():
		log.Info("Shutdown requested via context, shutting down...")
		grpcServer.GracefulStop()
		return ctx.Err()
	case <-shutdownCh:
		log.Info("Shutdown requested via gRPC, shutting down...")
		grpcServer.GracefulStop()
		return nil
	case err := <-errCh:
		return err
	}
}

func (gs *gatewayService) handleStatus(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"connected": true,
		"state":     "connected",
		"healthy":   true,
		"endpoint":  r.Host,
		"checkedAt": "",
	})
}

func (gs *gatewayService) handleHealth(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]interface{}{
		"ok":        true,
		"status":    "healthy",
		"checkedAt": "",
	})
}

func (gs *gatewayService) handleVersion(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/plain")
	w.Write([]byte("0.1.0"))
}

func (gs *gatewayService) readRequest(w http.ResponseWriter, r *http.Request, msg proto.Message) bool {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		http.Error(w, `{"error":"read body failed"}`, http.StatusBadRequest)
		return false
	}
	if err := protojson.Unmarshal(body, msg); err != nil {
		http.Error(w, fmt.Sprintf(`{"error":"unmarshal: %s"}`, err), http.StatusBadRequest)
		return false
	}
	return true
}

func (gs *gatewayService) writeResponse(w http.ResponseWriter, msg proto.Message) {
	w.Header().Set("Content-Type", "application/json")
	data, err := protojson.Marshal(msg)
	if err != nil {
		http.Error(w, fmt.Sprintf(`{"error":"marshal: %s"}`, err), http.StatusInternalServerError)
		return
	}
	w.Write(data)
}

func (gs *gatewayService) writeError(w http.ResponseWriter, err error) {
	st, _ := status.FromError(err)
	code := http.StatusInternalServerError
	if st.Code() == codes.NotFound {
		code = http.StatusNotFound
	} else if st.Code() == codes.InvalidArgument {
		code = http.StatusBadRequest
	}
	http.Error(w, fmt.Sprintf(`{"error":"%s","code":"%s"}`, st.Message(), st.Code().String()), code)
}

func (gs *gatewayService) handleGenerate(w http.ResponseWriter, r *http.Request) {
	var req pb.GenerateRequest
	if !gs.readRequest(w, r, &req) {
		return
	}

	stream := &jsonStreamWriter{writer: w, flusher: w.(http.Flusher)}
	w.Header().Set("Content-Type", "text/event-stream")
	w.Header().Set("Cache-Control", "no-cache")
	w.Header().Set("Connection", "keep-alive")

	if err := gs.api.Generate(&req, stream); err != nil {
		gs.writeError(w, err)
	}
}

func (gs *gatewayService) handleListModels(w http.ResponseWriter, r *http.Request) {
	resp, err := gs.api.ListModels(r.Context(), &pb.Empty{})
	if err != nil {
		gs.writeError(w, err)
		return
	}
	gs.writeResponse(w, resp)
}

func (gs *gatewayService) handleInstallModel(w http.ResponseWriter, r *http.Request) {
	var req pb.InstallRequest
	if !gs.readRequest(w, r, &req) {
		return
	}
	resp, err := gs.api.InstallModel(r.Context(), &req)
	if err != nil {
		gs.writeError(w, err)
		return
	}
	gs.writeResponse(w, resp)
}

func (gs *gatewayService) handleRemoveModel(w http.ResponseWriter, r *http.Request) {
	var req pb.RemoveRequest
	if !gs.readRequest(w, r, &req) {
		return
	}
	resp, err := gs.api.RemoveModel(r.Context(), &req)
	if err != nil {
		gs.writeError(w, err)
		return
	}
	gs.writeResponse(w, resp)
}

func (gs *gatewayService) handleGetDeviceInfo(w http.ResponseWriter, r *http.Request) {
	resp, err := gs.api.GetDeviceInfo(r.Context(), &pb.Empty{})
	if err != nil {
		gs.writeError(w, err)
		return
	}
	gs.writeResponse(w, resp)
}

func (gs *gatewayService) handleShutdown(w http.ResponseWriter, r *http.Request) {
	_, err := gs.api.Shutdown(r.Context(), &pb.Empty{})
	if err != nil {
		gs.writeError(w, err)
		return
	}
	w.Write([]byte(`{}`))
}

type jsonStreamWriter struct {
	writer  io.Writer
	flusher http.Flusher
	ctx     context.Context
}

func (j *jsonStreamWriter) Send(msg *pb.GenerateResponse) error {
	data, err := protojson.Marshal(msg)
	if err != nil {
		return err
	}
	_, err = fmt.Fprintf(j.writer, "data: %s\n\n", string(data))
	if err != nil {
		return err
	}
	if j.flusher != nil {
		j.flusher.Flush()
	}
	return nil
}

func (j *jsonStreamWriter) Context() context.Context {
	if j.ctx != nil {
		return j.ctx
	}
	return context.Background()
}

func (j *jsonStreamWriter) RecvMsg(m any) error { return nil }
func (j *jsonStreamWriter) SendMsg(m any) error { return nil }
func (j *jsonStreamWriter) SetHeader(md metadata.MD) error  { return nil }
func (j *jsonStreamWriter) SendHeader(md metadata.MD) error { return nil }
func (j *jsonStreamWriter) SetTrailer(md metadata.MD) {}
