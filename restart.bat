@echo off
echo Stopping Coffee Shop Management Application...
taskkill /f /im java.exe 2>nul
timeout /t 2 /nobreak >nul

echo Starting Coffee Shop Management Application...
cd /d "E:\CoffeeShopManagement"
start javaw -jar target\CoffeeShopManagement-0.0.1-SNAPSHOT.jar

echo Application restarted!
echo Access at: http://localhost:8080
echo Login: admin / admin123
pause 