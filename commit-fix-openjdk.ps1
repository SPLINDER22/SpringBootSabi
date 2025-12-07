# Script para hacer commit y push del fix de openjdk21
# Ejecutar con: .\commit-fix-openjdk.ps1

Write-Host "`n╔═══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                   ║" -ForegroundColor Cyan
Write-Host "║       🚀 COMMIT Y PUSH - FIX OPENJDK21            ║" -ForegroundColor Green
Write-Host "║                                                   ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

# Verificar que estamos en el directorio correcto
if (!(Test-Path "sabi")) {
    Write-Host "❌ Error: No estás en el directorio correcto" -ForegroundColor Red
    Write-Host "   Ejecuta: cd C:\Users\USER\Downloads\SpringBootSabi" -ForegroundColor Yellow
    exit 1
}

Write-Host "📁 Directorio verificado: SpringBootSabi" -ForegroundColor Green
Write-Host ""

# Mostrar estado de Git
Write-Host "📊 Estado actual de Git:" -ForegroundColor Yellow
git status --short
Write-Host ""

# Agregar archivos
Write-Host "➕ Agregando archivos..." -ForegroundColor Yellow
git add .
Write-Host "   ✓ Archivos agregados" -ForegroundColor Green
Write-Host ""

# Hacer commit
Write-Host "💾 Creando commit..." -ForegroundColor Yellow
git commit -m "Fix: Usar jdk genérico en lugar de jdk21 para Nixpacks

- Actualizado nixpacks.toml (raíz) y sabi/nixpacks.toml
- Nixpacks no reconoce 'openjdk21' ni 'jdk21'
- Usando 'jdk' genérico que sí está disponible
- Documentación actualizada en RESUMEN_FIX_OPENJDK.md"

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Commit creado exitosamente" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error al crear commit" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Push a GitHub
Write-Host "☁️  Subiendo a GitHub..." -ForegroundColor Yellow
git push origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✓ Push exitoso" -ForegroundColor Green
} else {
    Write-Host "   ❌ Error al hacer push" -ForegroundColor Red
    Write-Host "   Verifica tu conexión y credenciales de Git" -ForegroundColor Yellow
    exit 1
}
Write-Host ""

Write-Host "╔═══════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║                                                   ║" -ForegroundColor Cyan
Write-Host "║       ✅ PUSH COMPLETADO EXITOSAMENTE             ║" -ForegroundColor Green
Write-Host "║                                                   ║" -ForegroundColor Cyan
Write-Host "╚═══════════════════════════════════════════════════╝`n" -ForegroundColor Cyan

Write-Host "⏳ Railway está redesplegando automáticamente..." -ForegroundColor Yellow
Write-Host ""
Write-Host "📍 Próximos pasos:" -ForegroundColor Cyan
Write-Host "   1. Ve a https://railway.app/dashboard" -ForegroundColor White
Write-Host "   2. Click en tu proyecto" -ForegroundColor White
Write-Host "   3. Ve a 'Deployments' → View Logs" -ForegroundColor White
Write-Host "   4. Busca: 'Started SabiApplication'" -ForegroundColor White
Write-Host ""
Write-Host "⏱️  Tiempo estimado: 3-5 minutos" -ForegroundColor Yellow
Write-Host ""
Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

Read-Host "Presiona Enter para cerrar"

