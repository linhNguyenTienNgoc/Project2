@echo off
echo ========================================
echo Testing Dashboard Implementation
echo ========================================

echo.
echo 1. Cleaning project...
call mvn clean

echo.
echo 2. Compiling project...
call mvn compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Compilation failed!
    echo Please check the errors above.
    pause
    exit /b 1
)

echo.
echo ✅ Compilation successful!

echo.
echo 3. Running application...
echo Starting JavaFX application...
echo.
echo Instructions:
echo - Login with: admin / 123456
echo - Test tab switching (Menu/Bàn)
echo - Test order panel functionality
echo - Check for any runtime errors
echo.

call mvn javafx:run

echo.
echo Application finished.
pause
