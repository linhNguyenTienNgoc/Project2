@echo off
echo ========================================
echo    NOPITA CAFE - MENU SCREEN TEST
echo ========================================
echo.


echo Building project...
call mvn clean compile

if errorlevel 1 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo Starting Menu Screen...
echo.

REM Run the menu screen
java --module-path "lib/javafx-sdk-24.0.2/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes" com.cafe.MenuScreenLauncher

if errorlevel 1 (
    echo.
    echo ERROR: Failed to start Menu Screen
    pause
    exit /b 1
)

echo.
echo Menu Screen closed successfully
pause
