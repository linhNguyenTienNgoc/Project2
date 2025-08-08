@echo off
setlocal enabledelayedexpansion

REM Đặt tiêu đề cho cửa sổ
title Cafe Management System - Direct Launch

REM Chuyển đến thư mục chứa script này
cd /d "%~dp0"

echo ========================================
echo    Cafe Management System
echo ========================================
echo.
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

echo Starting application...
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

REM Chạy ứng dụng trực tiếp
mvn javafx:run

echo.
echo ========================================
echo Application has been closed.
echo ========================================
echo.
echo Thank you for using Cafe Management System!
echo.
pause

