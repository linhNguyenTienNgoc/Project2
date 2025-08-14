@echo off
echo ========================================
echo Running Cafe Management System
echo ========================================

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and add it to your PATH
    pause
    exit /b 1
)

REM Check if JAR file exists
if not exist "target\cafe-management-1.0.0.jar" (
    echo ERROR: JAR file not found
    echo Please build the project first using: scripts\build.bat
    pause
    exit /b 1
)

echo.
echo Starting Cafe Management System...
echo.

REM Run the application
java -jar target\cafe-management-1.0.0.jar

if errorlevel 1 (
    echo.
    echo ERROR: Application failed to start
    pause
    exit /b 1
)

echo.
echo Application closed successfully.
pause 