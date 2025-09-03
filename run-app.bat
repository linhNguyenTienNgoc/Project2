@echo off
echo ========================================
echo    Cafe Management System
echo ========================================
echo.
echo Starting application...
echo.


REM Clean và compile project
echo.
echo Cleaning and compiling project...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

REM Chạy ứng dụng bằng Maven
echo.
echo Starting application...
mvn javafx:run

echo.
echo Application closed.
pause 