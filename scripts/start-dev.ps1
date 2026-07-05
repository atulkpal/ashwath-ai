# start-dev.ps1 — Starts the engine and web dev server
# Run this from the repo root, or edit paths as needed.

$EngineDir = Join-Path $PSScriptRoot "..\engine"
$WebDir = Join-Path $PSScriptRoot "..\web"

Write-Host "=== Ashwath AI Dev Server ===" -ForegroundColor Cyan
Write-Host ""

# Start the engine in the background
Write-Host "[1/2] Starting engine on port 50051 (HTTP gateway on 50052)..." -ForegroundColor Yellow
$engineJob = Start-Job -ScriptBlock {
    param($dir)
    Set-Location $dir
    $env:ASHWATH_PORT = "50051"
    go run ./cmd/ashwathd/ --engine=mock
} -ArgumentList $EngineDir

Write-Host "  Engine PID: $($engineJob.Id)" -ForegroundColor Green
Start-Sleep -Seconds 2

# Start the web dev server
Write-Host "[2/2] Starting web dev server on port 5173..." -ForegroundColor Yellow
$webJob = Start-Job -ScriptBlock {
    param($dir)
    Set-Location $dir
    pnpm dev
} -ArgumentList $WebDir

Write-Host ""
Write-Host "=== Ready ===" -ForegroundColor Cyan
Write-Host "  Web app:  http://localhost:5173" -ForegroundColor Green
Write-Host "  Engine:   http://localhost:50051 (gRPC)" -ForegroundColor Green
Write-Host "  Gateway:  http://localhost:50052 (HTTP/JSON)" -ForegroundColor Green
Write-Host ""
Write-Host "Press Ctrl+C to stop both servers." -ForegroundColor Yellow

# Wait for either job to finish
Wait-Job -Job $engineJob, $webJob

# Cleanup
Stop-Job -Job $engineJob, $webJob
Remove-Job -Job $engineJob, $webJob
