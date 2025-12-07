# ============================================================
# SABI - Deploy to Railway Script
# ============================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SABI - Deploying to Railway" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 1. Change to sabi directory
Write-Host "📁 Changing to sabi directory..." -ForegroundColor Yellow
Set-Location -Path "sabi"

# 2. Check Git status
Write-Host "📊 Checking Git status..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "⚠️  WARNING: You are about to push changes to Railway" -ForegroundColor Red
Write-Host "This will trigger a new deployment." -ForegroundColor Red
Write-Host ""

$continue = Read-Host "Do you want to continue? (yes/no)"

if ($continue -ne "yes") {
    Write-Host "❌ Deployment cancelled" -ForegroundColor Red
    exit
}

# 3. Add all changes
Write-Host ""
Write-Host "📦 Adding all changes..." -ForegroundColor Yellow
git add .

# 4. Commit changes
Write-Host ""
Write-Host "💾 Committing changes..." -ForegroundColor Yellow
$commitMessage = Read-Host "Enter commit message (or press Enter for default)"
if ([string]::IsNullOrWhiteSpace($commitMessage)) {
    $commitMessage = "Fix DATABASE_URL parsing for Railway PostgreSQL"
}

git commit -m "$commitMessage"

# 5. Push to Railway
Write-Host ""
Write-Host "🚀 Pushing to Railway..." -ForegroundColor Yellow
git push origin main

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  ✅ Deploy completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Next steps:" -ForegroundColor Cyan
Write-Host "1. Check Railway dashboard for build progress" -ForegroundColor White
Write-Host "2. Wait 3-5 minutes for deployment to complete" -ForegroundColor White
Write-Host "3. Test your app: https://your-app.railway.app/health" -ForegroundColor White
Write-Host ""
Write-Host "💡 Tip: Run 'railway logs' to see deployment logs" -ForegroundColor Yellow
Write-Host ""

