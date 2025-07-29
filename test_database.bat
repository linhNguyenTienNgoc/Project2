@echo off
echo ========================================
echo Test Database Connection
echo ========================================

echo.
echo 1. Checking if application is running...
curl -s http://localhost:8080/test/database >nul 2>&1
if %errorlevel% equ 0 (
    echo Application is running!
    echo.
    echo 2. Testing database connection...
    curl -s http://localhost:8080/test/database
    echo.
    echo.
    echo 3. Testing connection string...
    curl -s http://localhost:8080/test/connection-string
    echo.
    echo.
    echo 4. Testing debug employees...
    curl -s http://localhost:8080/debug/employees
) else (
    echo Application is not running on port 8080!
    echo Please start the application first.
)

echo.
echo ========================================
echo Test completed!
echo ========================================
pause 