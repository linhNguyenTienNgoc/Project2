#!/bin/bash

echo ""
echo "====================================================="
echo "🎯 SETUP COMPLETE CAFE MANAGEMENT DATABASE"
echo "====================================================="
echo ""

echo "📋 Checking MySQL connection..."
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL not found! Please install MySQL first."
    echo "   Ubuntu/Debian: sudo apt install mysql-server"
    echo "   macOS: brew install mysql"
    echo "   CentOS/RHEL: sudo yum install mysql-server"
    exit 1
fi

echo "✅ MySQL found!"
echo ""

echo "🔧 Setting up database..."
echo "   - Creating database: cafe_management"
echo "   - Creating all tables"
echo "   - Inserting sample data"
echo ""

read -p "Enter MySQL username (default: root): " mysql_user
mysql_user=${mysql_user:-root}

read -s -p "Enter MySQL password: " mysql_password
echo ""

echo ""
echo "📦 Running complete database setup..."
mysql -u "$mysql_user" -p"$mysql_password" < complete_cafe_management.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ ====================================================="
    echo "✅ DATABASE SETUP COMPLETED SUCCESSFULLY!"
    echo "✅ ====================================================="
    echo ""
    echo "📊 Database Summary:"
    echo "   🗄️  Database: cafe_management"
    echo "   👥 Users: 7 sample accounts"
    echo "   🍰 Categories: 7 product categories"
    echo "   🍕 Products: 50+ diverse products"
    echo "   🏠 Areas: 5 different areas"
    echo "   🪑 Tables: 25+ tables"
    echo "   👤 Customers: 8 sample customers"
    echo "   📝 Orders: 10 sample orders with details"
    echo "   🎟️  Promotions: 7 active promotions"
    echo "   ⚙️  Settings: System configuration"
    echo ""
    echo "🚀 Ready to run JavaFX application!"
    echo ""
    echo "📝 Sample Login Accounts:"
    echo "   Admin: admin / 123456"
    echo "   Manager: manager / 123456"
    echo "   Cashier: cashier1 / 123456"
    echo "   Waiter: waiter1 / 123456"
    echo "   Barista: barista1 / 123456"
    echo ""
else
    echo ""
    echo "❌ ====================================================="
    echo "❌ DATABASE SETUP FAILED!"
    echo "❌ ====================================================="
    echo ""
    echo "🔍 Please check:"
    echo "   - MySQL username and password"
    echo "   - MySQL server is running"
    echo "   - No existing locks on cafe_management database"
    echo ""
fi

echo ""
read -p "Press Enter to continue..."
