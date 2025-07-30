@echo off
echo ========================================
echo    Coffee Shop Management - Optimized
echo ========================================
echo.

REM Set JAVA_HOME if not set
if "%JAVA_HOME%"=="" (
    echo Setting JAVA_HOME...
    set JAVA_HOME=C:\Users\%USERNAME%\.jdks\openjdk-24.0.1
    set PATH=%JAVA_HOME%\bin;%PATH%
)

REM JVM Performance Options
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -XX:+UseCompressedOops -XX:+UseCompressedClassPointers

REM JavaFX Performance Options
set JAVAFX_OPTS=--add-modules=javafx.controls,javafx.fxml -Djavafx.css.validation=false -Dprism.order=hw -Dprism.vsync=false -Djavafx.animation.fullspeed=true

REM Disable warnings
set WARNING_OPTS=--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED --add-opens=javafx.controls/javafx.scene.control=ALL-UNNAMED

REM Combine all options
set ALL_OPTS=%JAVA_OPTS% %JAVAFX_OPTS% %WARNING_OPTS%

echo Java Version:
java -version

echo.
echo JVM Options: %ALL_OPTS%
echo.

REM Build and run with JavaFX Maven Plugin
echo Building and running optimized application...
call mvnw clean javafx:run -Dargs="%ALL_OPTS%"

pause 