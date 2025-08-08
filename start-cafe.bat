@echo off
setlocal enabledelayedexpansion

REM Đặt tiêu đề cho cửa sổ
title Cafe Management System - Start Menu

REM Chuyển đến thư mục chứa script này
cd /d "%~dp0"

:menu
cls
echo ========================================
echo    Cafe Management System
echo ========================================
echo.
echo Choose an option:
echo.
echo [1] Run Application (Full Build)
echo [2] Quick Start (Skip Build)
echo [3] Build Only (No Run)
echo [4] Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" goto full_run
if "%choice%"=="2" goto quick_run
if "%choice%"=="3" goto build_only
if "%choice%"=="4" goto exit
echo Invalid choice. Please try again.
timeout /t 2 >nul
goto menu

:full_run
echo.
echo Starting full build and run...
call run-app.bat
goto end

:quick_run
echo.
echo Starting quick run...
call run-quick.bat
goto end

:build_only
echo.
echo Building project only...
call build-only.bat
goto end

:exit
echo.
echo Goodbye!
timeout /t 2 >nul
exit /b 0

:end
echo.
echo Press any key to return to menu...
pause >nul
goto menu

