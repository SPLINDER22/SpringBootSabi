# Script para subir cambios a Railway
# Ejecuta este script cuando quieras hacer cambios en producción

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "   SABI - Despliegue a Railway" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "sabi\pom.xml")) {
    Write-Host "❌ ERROR: Debes ejecutar este script desde el directorio SpringBootSabi" -ForegroundColor Red
    Write-Host "   Directorio actual: $(Get-Location)" -ForegroundColor Yellow
    Read-Host "Presiona Enter para salir"
    exit 1
}

Write-Host "📁 Directorio: $(Get-Location)" -ForegroundColor Green
Write-Host ""

# Verificar estado de git
Write-Host "🔍 Verificando cambios pendientes..." -ForegroundColor Yellow
git status --short

Write-Host ""
Write-Host "¿Deseas continuar con el despliegue? (S/N): " -ForegroundColor Cyan -NoNewline
$respuesta = Read-Host

if ($respuesta -ne "S" -and $respuesta -ne "s") {
    Write-Host "❌ Despliegue cancelado" -ForegroundColor Red
    Read-Host "Presiona Enter para salir"
    exit 0
}

Write-Host ""
Write-Host "📝 Ingresa el mensaje de commit (o presiona Enter para usar mensaje por defecto): " -ForegroundColor Cyan -NoNewline
$mensaje = Read-Host

if ([string]::IsNullOrWhiteSpace($mensaje)) {
    $mensaje = "Update: mejoras y configuración"
}

Write-Host ""
Write-Host "🔨 Agregando archivos..." -ForegroundColor Yellow
git add .

Write-Host "💾 Creando commit..." -ForegroundColor Yellow
git commit -m "$mensaje"

Write-Host "🚀 Subiendo a GitHub..." -ForegroundColor Yellow
git push origin main

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "✅ ¡Despliegue completado!" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""
Write-Host "Railway detectará los cambios automáticamente y redesplegará tu aplicación." -ForegroundColor Cyan
Write-Host "Puedes ver el progreso en: https://railway.app/dashboard" -ForegroundColor Cyan
Write-Host ""
Write-Host "⏱️  El despliegue tomará aproximadamente 2-3 minutos." -ForegroundColor Yellow
Write-Host ""

Read-Host "Presiona Enter para salir"

