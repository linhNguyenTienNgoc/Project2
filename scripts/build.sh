#!/bin/bash

echo "========================================"
echo "Building Cafe Management System"
echo "========================================"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

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

echo ""
echo "Cleaning previous build..."
mvn clean

echo ""
echo "Compiling and testing..."
mvn compile test

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Build failed during compilation or testing"
    exit 1
fi

echo ""
echo "Building JAR file..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Failed to create JAR file"
    exit 1
fi

echo ""
echo "========================================"
echo "Build completed successfully!"
echo "========================================"
echo ""
echo "JAR file location: target/cafe-management-1.0.0.jar"
echo ""
echo "To run the application:"
echo "  java -jar target/cafe-management-1.0.0.jar"
echo "" 