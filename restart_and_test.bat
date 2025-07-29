@echo off
echo ========================================
echo Coffee Shop Management - Restart & Test
echo ========================================

echo.
echo 1. Stopping any running application on port 8080...
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
echo 5. Testing application...
echo Testing database connection...
curl -s http://localhost:8080/debug/employees

echo.
echo 6. Testing specific user...
curl -s "http://localhost:8080/debug/user?username=admin"

echo.
echo 7. Testing login...
curl -s "http://localhost:8080/debug/test-login?username=admin&password=admin123"

echo.
echo ========================================
echo Test completed!
echo ========================================
echo.
echo You can now:
echo 1. Open http://localhost:8080 in your browser
echo 2. Try to login with:
echo    - Username: admin
echo    - Password: admin123
echo.
echo Or test other accounts:
echo    - Username: staff1, Password: staff123
echo    - Username: manager, Password: manager123
echo.
pause 