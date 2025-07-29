@echo off
echo ========================================
echo FIX LOGIN - Coffee Shop Management
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
echo 3. IMPORTANT: Please run fix_login.sql in SQL Server Management Studio
echo    - Open SQL Server Management Studio
echo    - Connect to M12345\M12345
echo    - Open file fix_login.sql
echo    - Execute the script (F5)
echo    - Wait for completion
echo.
pause

echo.
echo 4. Starting application...
start "Coffee Shop Management" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo 5. Waiting for application to start (45 seconds)...
timeout /t 45 /nobreak >nul

echo.
echo 6. Testing database connection...
curl -s http://localhost:8080/test/database

echo.
echo 7. Testing debug employees...
curl -s http://localhost:8080/debug/employees

echo.
echo 8. Testing specific admin user...
curl -s "http://localhost:8080/debug/user?username=admin"

echo.
echo ========================================
echo FIX COMPLETED!
echo ========================================
echo.
echo Now try to login at: http://localhost:8080
echo.
echo Login credentials:
echo - Username: admin
echo - Password: admin123
echo.
echo Or:
echo - Username: staff1
echo - Password: staff123
echo.
echo ========================================
pause 