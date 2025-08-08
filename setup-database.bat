@echo off
echo Setting up Cafe Management Database...
echo.

REM Check if MySQL is available
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL is not installed or not in PATH
    echo Please install MySQL and add it to your PATH
    pause
    exit /b 1
)

echo Creating database and tables...
mysql -u root -p12345678 < database/cafe_management.sql

if %errorlevel% equ 0 (
    echo Database created successfully!
    echo.
    echo Inserting sample data...
    mysql -u root -p12345678 < database/data.sql
    
    if %errorlevel% equ 0 (
        echo Sample data inserted successfully!
        echo.
        echo Database setup completed!
    ) else (
        echo ERROR: Failed to insert sample data
    )
) else (
    echo ERROR: Failed to create database
)

echo.
pause
