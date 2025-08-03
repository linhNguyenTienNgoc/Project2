@echo off
echo ========================================
echo Building Cafe Management System
echo ========================================

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and add it to your PATH
    pause
    exit /b 1
)

echo.
echo Cleaning previous build...
call mvn clean

echo.
echo Compiling and testing...
call mvn compile test

if errorlevel 1 (
    echo.
    echo ERROR: Build failed during compilation or testing
    pause
    exit /b 1
)

echo.
echo Building JAR file...
call mvn package -DskipTests

if errorlevel 1 (
    echo.
    echo ERROR: Failed to create JAR file
    pause
    exit /b 1
)

echo.
echo ========================================
echo Build completed successfully!
echo ========================================
echo.
echo JAR file location: target\cafe-management-1.0.0.jar
echo.
echo To run the application:
echo   java -jar target\cafe-management-1.0.0.jar
echo.
pause 