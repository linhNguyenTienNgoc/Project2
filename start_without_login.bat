@echo off
echo ========================================
echo Coffee Shop Management - No Login Mode
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
echo 3. Starting application without login...
start "Coffee Shop Management" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo 4. Waiting for application to start (30 seconds)...
timeout /t 30 /nobreak >nul

echo.
echo ========================================
echo APPLICATION STARTED!
echo ========================================
echo.
echo Now you can access:
echo 1. Dashboard: http://localhost:8080/dashboard
echo 2. Menu: http://localhost:8080/menu
echo 3. Tables: http://localhost:8080/tables
echo 4. Invoices: http://localhost:8080/invoices
echo 5. Employees: http://localhost:8080/employees
echo 6. Reports: http://localhost:8080/reports
echo.
echo No login required - Direct access!
echo ========================================
pause 