#!/bin/bash

echo "========================================"
echo "Running Cafe Management System"
echo "========================================"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher and add it to your PATH"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "ERROR: Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

# Check if JAR file exists
if [ ! -f "target/cafe-management-1.0.0.jar" ]; then
    echo "ERROR: JAR file not found"
    echo "Please build the project first using: ./scripts/build.sh"
    exit 1
fi

echo ""
echo "Starting Cafe Management System..."
echo ""

# Run the application
java -jar target/cafe-management-1.0.0.jar

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Application failed to start"
    exit 1
fi

echo ""
echo "Application closed successfully." 