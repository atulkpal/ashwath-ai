import { spawn, type ChildProcess } from "node:child_process"
import { resolve } from "node:path"
import type { Plugin } from "vite"

const engineDir = resolve(import.meta.dirname, "..", "engine")

let engineProcess: ChildProcess | null = null

function getPort(): number {
  const envPort = process.env.ASHWATH_PORT
  return envPort ? parseInt(envPort, 10) : 50051
}

function startEngine(): void {
  const port = getPort()
  console.log(`\n  ⚡ Starting Ashwath Engine on port ${port}...`)

  engineProcess = spawn("go", ["run", "./cmd/ashwathd/", "--engine=mock"], {
    cwd: engineDir,
    stdio: ["ignore", "inherit", "inherit"],
    env: { ...process.env, ASHWATH_PORT: String(port) },
    shell: true,
  })

  engineProcess.on("error", (err) => {
    console.error(`  ⚡ Engine failed to start: ${err.message}`)
    console.log("  ⚡ Make sure Go is installed and the engine compiles.")
  })

  engineProcess.on("exit", (code) => {
    if (code !== null && code !== 0) {
      console.error(`  ⚡ Engine exited with code ${code}`)
    }
    engineProcess = null
  })
}

function stopEngine(): void {
  if (engineProcess) {
    console.log("\n  ⚡ Stopping Ashwath Engine...")
    if (process.platform === "win32") {
      spawn("taskkill", ["/pid", String(engineProcess.pid), "/f", "/t"])
    } else {
      engineProcess.kill("SIGTERM")
    }
    engineProcess = null
  }
}

export function enginePlugin(): Plugin {
  return {
    name: "ashwath-engine",

    configureServer(server) {
      startEngine()

      server.httpServer?.once("close", () => {
        stopEngine()
      })
    },

    closeBundle() {
      stopEngine()
    },
  }
}
