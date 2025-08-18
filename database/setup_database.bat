@echo off
chcp 65001 >nul
echo.
echo =====================================================
echo 🎯 SETUP COMPLETE CAFE MANAGEMENT DATABASE
echo =====================================================
echo.

echo 📋 Checking MySQL connection...
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ MySQL not found! Please install MySQL first.
    echo    Download: https://dev.mysql.com/downloads/mysql/
    pause
    exit /b 1
)

echo ✅ MySQL found!
echo.

echo 🔧 Setting up database...
echo    - Creating database: cafe_management
echo    - Creating all tables
echo    - Inserting sample data
echo.

set /p mysql_user="Enter MySQL username (default: root): "
if "%mysql_user%"=="" set mysql_user=root

set /p mysql_password="Enter MySQL password: "

echo.
echo 📦 Running complete database setup...
mysql -u %mysql_user% -p%mysql_password% < complete_cafe_management.sql

if %errorlevel% equ 0 (
    echo.
    echo ✅ =====================================================
    echo ✅ DATABASE SETUP COMPLETED SUCCESSFULLY!
    echo ✅ =====================================================
    echo.
    echo 📊 Database Summary:
    echo    🗄️  Database: cafe_management
    echo    👥 Users: 7 sample accounts
    echo    🍰 Categories: 7 product categories  
    echo    🍕 Products: 50+ diverse products
    echo    🏠 Areas: 5 different areas
    echo    🪑 Tables: 25+ tables
    echo    👤 Customers: 8 sample customers
    echo    📝 Orders: 10 sample orders with details
    echo    🎟️  Promotions: 7 active promotions
    echo    ⚙️  Settings: System configuration
    echo.
    echo 🚀 Ready to run JavaFX application!
    echo.
    echo 📝 Sample Login Accounts:
    echo    Admin: admin / 123456
    echo    Manager: manager / 123456
    echo    Cashier: cashier1 / 123456
    echo    Waiter: waiter1 / 123456
    echo    Barista: barista1 / 123456
    echo.
) else (
    echo.
    echo ❌ =====================================================
    echo ❌ DATABASE SETUP FAILED!
    echo ❌ =====================================================
    echo.
    echo 🔍 Please check:
    echo    - MySQL username and password
    echo    - MySQL server is running
    echo    - No existing locks on cafe_management database
    echo.
)

echo.
pause
