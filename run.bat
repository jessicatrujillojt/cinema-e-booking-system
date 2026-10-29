@echo off
cd /d "%~dp0"

echo Starting backend...
start "Cinema Backend" cmd /k "cd backend && mvnw.cmd spring-boot:run"

echo Starting frontend...
start "Cinema Frontend" cmd /k "python -m http.server 5501"

echo Backend: http://localhost:8080
echo Frontend: http://localhost:5501/home.html