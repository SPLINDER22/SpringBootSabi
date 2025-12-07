# Fix crítico: DATABASE_URL parsing mejorado para Railway
# Este script hace commit y push de los cambios

Write-Host "🚀 Preparando deploy del fix de DATABASE_URL..." -ForegroundColor Cyan

# Cambiar al directorio del proyecto
Set-Location "C:\Users\USER\Downloads\SpringBootSabi\sabi"

# Ver el estado actual
Write-Host "`n📋 Estado de Git:" -ForegroundColor Yellow
git status

# Agregar los archivos modificados
Write-Host "`n➕ Agregando cambios..." -ForegroundColor Yellow
git add src/main/java/com/sabi/sabi/config/DataSourceConfig.java
git add src/main/resources/application-prod.properties

# Ver los cambios
Write-Host "`n📝 Cambios agregados:" -ForegroundColor Yellow
git diff --cached --stat

# Confirmar
Write-Host "`n❓ ¿Deseas hacer commit y push? (S/N)" -ForegroundColor Green
$respuesta = Read-Host

if ($respuesta -eq "S" -or $respuesta -eq "s") {
    # Hacer commit
    Write-Host "`n💾 Haciendo commit..." -ForegroundColor Yellow
    git commit -m "Fix: DATABASE_URL parsing mejorado con múltiples formatos

- Manejo de DATABASE_URL con y sin credenciales en la URL
- Soporte para formato jdbc: y postgresql://
- Fallback a variables de entorno PGUSER y PGPASSWORD
- Mejor logging para debugging
- Eliminada configuración duplicada en properties"

    # Push
    Write-Host "`n📤 Haciendo push a Railway..." -ForegroundColor Yellow
    git push origin main

    Write-Host "`n✅ Deploy iniciado en Railway!" -ForegroundColor Green
    Write-Host "   Revisa los logs en: https://railway.app/dashboard" -ForegroundColor Cyan
    Write-Host "`n⏱️  El deploy tomará 3-5 minutos" -ForegroundColor Yellow

} else {
    Write-Host "`n❌ Operación cancelada" -ForegroundColor Red
}

Write-Host "`n✨ Script finalizado" -ForegroundColor Cyan

