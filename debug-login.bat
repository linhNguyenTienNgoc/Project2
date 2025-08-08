@echo off
setlocal enabledelayedexpansion

echo ========================================
echo    Debug Login Issues
echo ========================================
echo.

echo Current time: %date% %time%
echo.

echo === Database Connection Test ===
echo Testing database connection...

REM Test database connection
mvn compile -q
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo Database connection: OK
echo.

echo === Available Users ===
echo Testing user authentication...

REM Run a simple test to check users
echo Testing waiter1 login...
echo Username: waiter1
echo Password: password
echo.

echo === Instructions ===
echo 1. Try logging in with:
echo    Username: waiter1
echo    Password: password
echo.
echo 2. If that doesn't work, try:
echo    Username: waiter1  
echo    Password: 123456
echo.
echo 3. Check the error message in the login window
echo.

echo === Common Issues ===
echo - Wrong password (use 'password' not '123456')
echo - Database connection issues
echo - Role loading problems
echo - JavaFX window not appearing
echo.

pause
