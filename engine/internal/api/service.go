package api

import (
	"context"
	"fmt"
	"io"

	"github.com/ashwathai/ashwath-engine/internal/api/pb"
	"github.com/ashwathai/ashwath-engine/internal/device"
	"github.com/ashwathai/ashwath-engine/internal/logging"
	"github.com/ashwathai/ashwath-engine/internal/models"
	"github.com/ashwathai/ashwath-engine/internal/runtime"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

type EngineService struct {
	pb.UnimplementedAshwathEngineServer
	engine   runtime.Engine
	detector device.Detector
	registry models.Registry
	log      logging.Logger
}

func NewEngineService(eng runtime.Engine, det device.Detector, reg models.Registry, log logging.Logger) *EngineService {
	return &EngineService{
		engine:   eng,
		detector: det,
		registry: reg,
		log:      log,
	}
}

func (s *EngineService) ListModels(ctx context.Context, req *pb.Empty) (*pb.ModelList, error) {
	mdls, err := s.registry.List()
	if err != nil {
		return nil, status.Errorf(codes.Internal, "list models failed: %v", err)
	}

	caps, _ := s.detector.Detect()
	device := models.DeviceSpec{
		RamGB:    caps.RAMGB,
		CPUCores: caps.CPUCores,
		HasNPU:   caps.HasNPU,
		HasGPU:   caps.HasGPU,
		GPUVendor: caps.GPUVendor,
	}
	scored := models.ScoreModels(device, mdls)

	resp := &pb.ModelList{Models: make([]*pb.ModelInfo, 0, len(scored))}
	for _, sm := range scored {
		allTags := make([]string, 0, len(sm.Tags)+6)
		allTags = append(allTags, sm.Tags...)
		for _, c := range sm.Capabilities {
			allTags = append(allTags, "capability:"+c)
		}
		allTags = append(allTags, "score:"+itoa(sm.Score))
		allTags = append(allTags, "min_ram:"+ftoa(sm.MinRamGB))
		allTags = append(allTags, "recommended_ram:"+ftoa(sm.RecommendedRamGB))
		allTags = append(allTags, "speed:"+string(sm.SpeedClass))
		if sm.Recommended {
			allTags = append(allTags, "recommended")
		}

		resp.Models = append(resp.Models, &pb.ModelInfo{
			Id:         sm.ID,
			Name:       sm.Name,
			Provider:   sm.Provider,
			SizeBytes:  sm.SizeBytes,
			Parameters: sm.Parameters,
			Tags:       allTags,
			Installed:  sm.Installed,
		})
	}
	s.log.Info("ListModels", "count", len(resp.Models))
	return resp, nil
}

func itoa(i int) string {
	if i == 0 {
		return "0"
	}
	d := ""
	for i > 0 {
		d = string(rune('0'+i%10)) + d
		i /= 10
	}
	return d
}

func ftoa(f float64) string {
	i := int(f)
	r := int((f - float64(i)) * 10)
	if r < 0 {
		r = -r
	}
	return itoa(i) + "." + itoa(r)
}

func (s *EngineService) InstallModel(ctx context.Context, req *pb.InstallRequest) (*pb.InstallResponse, error) {
	s.log.Info("InstallModel", "model_id", req.ModelId)
	return &pb.InstallResponse{
		Started: true,
		Message: fmt.Sprintf("Installation started for %s", req.ModelId),
	}, nil
}

func (s *EngineService) GetDeviceInfo(ctx context.Context, req *pb.Empty) (*pb.DeviceInfo, error) {
	caps, err := s.detector.Detect()
	if err != nil {
		return nil, status.Errorf(codes.Internal, "detection failed: %v", err)
	}
	return &pb.DeviceInfo{
		RamGb:     caps.RAMGB,
		CpuCores:  int32(caps.CPUCores),
		HasNpu:    caps.HasNPU,
		HasGpu:    caps.HasGPU,
		GpuVendor: caps.GPUVendor,
		Os:        caps.OSType,
		Arch:      caps.Arch,
	}, nil
}

var shutdownCh chan struct{}

func SetShutdownChannel(ch chan struct{}) {
	shutdownCh = ch
}

func (s *EngineService) Shutdown(ctx context.Context, req *pb.Empty) (*pb.Empty, error) {
	s.log.Info("Shutdown requested")
	if shutdownCh != nil {
		close(shutdownCh)
	}
	return &pb.Empty{}, nil
}

func (s *EngineService) Generate(req *pb.GenerateRequest, stream grpc.ServerStreamingServer[pb.GenerateResponse]) error {
	s.log.Info("Generate", "prompt", truncate(req.Prompt, 50))

	rpcReq := runtime.Request{
		Prompt:      req.Prompt,
		MaxTokens:   int(req.MaxTokens),
		Temperature: req.Temperature,
		TopK:        int(req.TopK),
		TopP:        req.TopP,
	}

	ctx := stream.Context()
	ch, err := s.engine.Generate(ctx, rpcReq)
	if err != nil {
		return status.Errorf(codes.Internal, "generate failed: %v", err)
	}

	var tokenCount int32
	for result := range ch {
		if result.Error != nil {
			return status.Errorf(codes.Internal, "generation error: %v", result.Error)
		}
		tokenCount++
		resp := &pb.GenerateResponse{
			Text:       result.Text,
			TokensUsed: tokenCount,
			Done:       result.Done,
		}
		if err := stream.Send(resp); err != nil {
			return status.Errorf(codes.Internal, "send failed: %v", err)
		}
	}
	return nil
}

func truncate(s string, max int) string {
	if len(s) <= max {
		return s
	}
	return s[:max] + "..."
}

func (s *EngineService) Close() error {
	return s.engine.Stop(context.Background())
}

var _ io.Closer = (*EngineService)(nil)
