# 🚀 Script de Deploy con MySQL para Railway
# Este script hace commit y push de los cambios de configuración de MySQL

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  SABI - Deploy MySQL Configuration" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Cambiar al directorio raíz del proyecto
$rootDir = "C:\Users\USER\Downloads\SpringBootSabi"
Set-Location $rootDir

Write-Host "📍 Current directory: $(Get-Location)" -ForegroundColor Yellow
Write-Host ""

# Verificar que estamos en un repositorio Git
if (-not (Test-Path ".git")) {
    Write-Host "❌ Error: Not a Git repository" -ForegroundColor Red
    Write-Host "Please run this script from the project root directory" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Git repository detected" -ForegroundColor Green
Write-Host ""

# Mostrar el estado actual
Write-Host "📋 Current Git status:" -ForegroundColor Yellow
git status --short
Write-Host ""

# Agregar archivos modificados
Write-Host "➕ Adding files to Git..." -ForegroundColor Yellow
git add sabi/src/main/java/com/sabi/sabi/config/DataSourceConfig.java
git add URGENTE_MYSQL_PASSWORD_RAILWAY.md
git add .
Write-Host "✅ Files added" -ForegroundColor Green
Write-Host ""

# Crear commit
Write-Host "📝 Creating commit..." -ForegroundColor Yellow
$commitMessage = "fix: Debug MySQL password issue - show ALL env variables

- Print ALL environment variables for debugging
- Add password length verification
- Enhanced MySQL configuration logging
- Add urgent password configuration guide
- This will help identify if password is set correctly"

git commit -m $commitMessage
Write-Host "✅ Commit created" -ForegroundColor Green
Write-Host ""

# Push a Railway
Write-Host "🚀 Pushing to Railway..." -ForegroundColor Yellow
Write-Host ""

try {
    git push origin main
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "  ✅ DEPLOY SUCCESSFUL!" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "🚨 URGENT: Check Password Configuration" -ForegroundColor Red
    Write-Host ""
    Write-Host "The error 'Access denied' means MySQL PASSWORD is WRONG!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "📋 Required Steps:" -ForegroundColor Cyan
    Write-Host "1. Go to Railway Dashboard" -ForegroundColor White
    Write-Host "2. Click on MySQL service" -ForegroundColor White
    Write-Host "3. Go to Variables tab" -ForegroundColor White
    Write-Host "4. COPY the MYSQLPASSWORD value (click to copy)" -ForegroundColor White
    Write-Host "5. Go to springbootsabi service" -ForegroundColor White
    Write-Host "6. Variables → Add Variable" -ForegroundColor White
    Write-Host "7. Name: MYSQLPASSWORD" -ForegroundColor White
    Write-Host "8. Value: PASTE the exact password" -ForegroundColor White
    Write-Host "9. Make sure NO spaces before/after" -ForegroundColor White
    Write-Host ""
    Write-Host "📖 Read the complete guide:" -ForegroundColor Cyan
    Write-Host "   URGENTE_MYSQL_PASSWORD_RAILWAY.md" -ForegroundColor White
    Write-Host ""
    Write-Host "🔍 After configuring, check logs for:" -ForegroundColor Yellow
    Write-Host '   "Password length: XX characters"' -ForegroundColor White
    Write-Host '   "✅ MySQL DataSource configured successfully!"' -ForegroundColor White
    Write-Host ""

} catch {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "  ❌ DEPLOY FAILED!" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "💡 Try:" -ForegroundColor Yellow
    Write-Host "   git push origin main --force" -ForegroundColor White
    Write-Host ""
    exit 1
}

Write-Host "🎯 Monitor the deployment:" -ForegroundColor Yellow
Write-Host "   Railway Dashboard → Deployments → View Logs" -ForegroundColor White
Write-Host ""
Write-Host "🔍 Look for this in the logs:" -ForegroundColor Yellow
Write-Host '   "📋 ALL MYSQL-related Environment Variables:"' -ForegroundColor White
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

