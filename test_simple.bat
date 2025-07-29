@echo off
echo ========================================
echo Test Simple Pages - Coffee Shop Management
echo ========================================

echo.
echo 1. Stopping any running application...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Stopping process PID: %%a
    taskkill /F /PID %%a 2>nul
)

echo.
echo 2. Waiting 3 seconds...
timeout /t 3 /nobreak >nul

echo.
echo 3. Starting application...
start "Coffee Shop Management" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo 4. Waiting for application to start (30 seconds)...
timeout /t 30 /nobreak >nul

echo.
echo ========================================
echo TESTING SIMPLE PAGES
echo ========================================
echo.
echo Now test these URLs in your browser:
echo.
echo 1. Home page: http://localhost:8080/
echo 2. Simple menu: http://localhost:8080/simple-menu
echo 3. Test page: http://localhost:8080/test
echo.
echo If these work, then the issue is with database connection.
echo If these don't work, then there's a basic application issue.
echo.
echo ========================================
pause 