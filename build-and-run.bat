@echo off
echo ========================================
echo    Cafe Management System
echo    Build and Run
echo ========================================
echo.

REM Kiểm tra Java
echo Checking Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Kiểm tra Maven
echo Checking Maven...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    pause
    exit /b 1
)

echo All checks passed!
echo.

REM Clean và build project
echo Building project...
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo Build completed successfully!
echo.

REM Chạy JAR file
echo Starting application...
java -jar target/cafe-management-1.0.0.jar

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