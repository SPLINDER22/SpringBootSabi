#!/bin/bash
set -e

echo "============================================"
echo "  SABI - Railway Build Script"
echo "============================================"
echo ""

echo "📍 Current directory: $(pwd)"
echo "📁 Contents:"
ls -la

echo ""
echo "☕ Java version:"
java -version

echo ""
echo "📦 Maven version:"
mvn --version

echo ""
echo "🔨 Building project..."
cd sabi

echo "📍 Now in: $(pwd)"
echo "📁 Contents:"
ls -la

echo ""
echo "🚀 Starting Maven build..."
mvn clean package -DskipTests -B -e

echo ""
echo "✅ Build completed successfully!"
echo "📦 JAR file:"
ls -lh target/*.jar

echo ""
echo "============================================"
echo "  Build finished successfully"
echo "============================================"

