@echo off
echo ========================================
echo    Cafe Management System
echo ========================================
echo.

REM Kiểm tra Java
echo Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or later
    pause
    exit /b 1
)

echo All checks passed!
echo.
echo Starting application...
echo.

REM Chạy ứng dụng
mvn javafx:run

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo Application closed successfully.
pause 