@echo off
setlocal enabledelayedexpansion

REM Đặt tiêu đề cho cửa sổ
title Cafe Management System - Quick Launch

REM Chuyển đến thư mục chứa script này
cd /d "%~dp0"

echo ========================================
echo    Cafe Management System
echo ========================================
echo.
echo Quick Launch Mode
echo Current directory: %CD%
echo.

REM Kiểm tra xem có file pom.xml không
if not exist "pom.xml" (
    echo ERROR: pom.xml not found in current directory
    echo Please make sure you are running this script from the project root directory
    echo.
    pause
    exit /b 1
)

REM Kiểm tra xem đã compile chưa
if not exist "target\classes" (
    echo WARNING: Project not compiled yet
    echo Running full build first...
    echo.
    call run-app.bat
    exit /b %errorlevel%
)

echo Checking system requirements...
echo.

REM Kiểm tra Java version
echo [1/3] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 or later and add it to your PATH
    echo.
    pause
    exit /b 1
)
echo ✓ Java is installed

REM Kiểm tra Maven
echo.
echo [2/3] Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Apache Maven and add it to your PATH
    echo.
    pause
    exit /b 1
)
echo ✓ Maven is installed

REM Chạy ứng dụng bằng Maven (không compile lại)
echo.
echo [3/3] Starting application...
echo ========================================
echo    Launching Cafe Management System
echo ========================================
echo.
echo The application is starting...
echo Please wait for the login window to appear.
echo.
echo To close the application, close the JavaFX window.
echo To stop this launcher, press Ctrl+C
echo.

REM Chạy ứng dụng với output đầy đủ
mvn javafx:run

echo.
echo ========================================
echo Application has been closed.
echo ========================================
echo.
echo Thank you for using Cafe Management System!
echo.
pause

