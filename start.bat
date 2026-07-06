@echo off
cd /d "%~dp0"
echo Starting Ashwath AI Engine + Web...
start "Engine" cmd /c "cd engine && go run ./cmd/ashwathd/ --engine=mock"
timeout /t 3 /nobreak >nul
cd web
start pnpm dev
echo.
echo Web app should open at http://localhost:5173
echo Close this window to stop both servers.
echo.
pause
taskkill /f /fi "WINDOWTITLE eq Engine" >nul 2>&1
