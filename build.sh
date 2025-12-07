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
echo "🔍 Environment variables:"
echo "PORT: ${PORT:-not set}"
echo "DATABASE_URL: ${DATABASE_URL:+configured}"
echo "SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-not set}"

echo ""
echo "🔨 Building project..."
cd sabi

echo "📍 Now in: $(pwd)"
echo "📁 Contents:"
ls -la

echo ""
echo "🚀 Starting Maven build..."
mvn clean package -DskipTests -B -e 2>&1 | tee build.log || {
    echo ""
    echo "❌ Build failed! Last 50 lines of output:"
    tail -50 build.log
    exit 1
}

echo ""
echo "✅ Build completed successfully!"
echo "📦 JAR file:"
ls -lh target/*.jar

echo ""
echo "============================================"
echo "  Build finished successfully"
echo "============================================"

