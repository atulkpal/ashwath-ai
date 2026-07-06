param(
  [string]$Engine = "mock",
  [string]$Model = ""
)

Write-Host "╔══════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║        Ashwath AI — Web             ║" -ForegroundColor Cyan
Write-Host "╚══════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$WebDir = Join-Path $PSScriptRoot "web"
$EngineDir = Join-Path $PSScriptRoot "engine"

# Start engine in background
Write-Host "→ Starting engine..." -ForegroundColor Yellow
$engineArgs = @("run", "./cmd/ashwathd/", "--engine=$Engine")
if ($Model) { $engineArgs += @("--model=$Model") }

$env:ASHWATH_PORT = "50051"
$engineJob = Start-Job -Name "ashwath-engine" -ScriptBlock {
  param($d, $args)
  Set-Location $d
  go run ./cmd/ashwathd/ --engine=$using:Engine $(if ($using:Model) { "--model=$using:Model" })
} -ArgumentList $EngineDir, $engineArgs

Start-Sleep -Seconds 2

# Start web dev server
Write-Host "→ Starting web app..." -ForegroundColor Yellow
$webJob = Start-Job -Name "ashwath-web" -ScriptBlock {
  param($d)
  Set-Location $d
  pnpm dev
} -ArgumentList $WebDir

Start-Sleep -Seconds 3

Write-Host "╔══════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  Ready! Open http://localhost:5173    ║" -ForegroundColor Green
Write-Host "╚══════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "Press any key to stop both servers..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Cleanup
Write-Host "→ Stopping..." -ForegroundColor Yellow
Stop-Job $engineJob -ErrorAction SilentlyContinue
Stop-Job $webJob -ErrorAction SilentlyContinue
Remove-Job $engineJob -ErrorAction SilentlyContinue
Remove-Job $webJob -ErrorAction SilentlyContinue
Write-Host "Done." -ForegroundColor Green
