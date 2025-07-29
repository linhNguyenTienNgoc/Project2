@echo off
echo ========================================
echo RESTORE LOGIN SYSTEM - Coffee Shop Management
echo ========================================

echo.
echo 1. Stopping any running application...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Stopping process PID: %%a
    taskkill /F /PID %%a 2>nul
)

echo.
echo 2. IMPORTANT: Please run fix_login.sql in SQL Server Management Studio
echo    - Open SQL Server Management Studio
echo    - Connect to M12345\M12345
echo    - Open file fix_login.sql
echo    - Execute the script (F5)
echo    - Wait for completion
echo.
pause

echo.
echo 3. Starting application...
start "Coffee Shop Management" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo 4. Waiting for application to start (45 seconds)...
timeout /t 45 /nobreak >nul

echo.
echo ========================================
echo LOGIN SYSTEM RESTORED!
echo ========================================
echo.
echo Now test login at: http://localhost:8080/login
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
echo TESTING STEPS:
echo 1. Go to http://localhost:8080/login
echo 2. Enter username: admin
echo 3. Enter password: admin123
echo 4. Click Login
echo 5. Should redirect to dashboard
echo ========================================
pause 