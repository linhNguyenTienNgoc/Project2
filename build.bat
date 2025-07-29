@echo off
echo Setting JAVA_HOME to Java 17...
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

echo Current Java version:
java -version

echo Building project with Maven...
call mvnw.cmd clean compile

echo Build completed!
pause 