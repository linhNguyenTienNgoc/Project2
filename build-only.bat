@echo off
setlocal enabledelayedexpansion

REM Đặt tiêu đề cho cửa sổ
title Cafe Management System - Build Only

REM Chuyển đến thư mục chứa script này
cd /d "%~dp0"

echo ========================================
echo    Cafe Management System
echo ========================================
echo.
echo Build Only Mode
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
java -version 2>&1 | findstr "version"

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
mvn -version 2>&1 | findstr "Apache Maven"

REM Clean và compile project
echo.
echo [3/3] Building project...
echo Cleaning and compiling project...
mvn clean compile
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Project compilation failed
    echo Please check the error messages above
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✓ Project built successfully!
echo ========================================
echo.
echo The project has been compiled and is ready to run.
echo You can now use 'run-quick.bat' for faster startup.
echo.
pause

