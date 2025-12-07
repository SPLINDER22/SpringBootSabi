#!/usr/bin/env pwsh
# Script de verificación pre-deploy para Railway
# Ejecutar antes de subir a Railway

Write-Host "🔍 VERIFICACIÓN PRE-DEPLOY PARA RAILWAY" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$errores = 0
$advertencias = 0

# 1. Verificar archivos necesarios
Write-Host "📁 Verificando archivos necesarios..." -ForegroundColor Yellow

$archivosNecesarios = @(
    "Procfile",
    "railway.json",
    "nixpacks.toml",
    "pom.xml",
    "src/main/resources/application-prod.properties",
    ".gitignore"
)

foreach ($archivo in $archivosNecesarios) {
    if (Test-Path $archivo) {
        Write-Host "  ✅ $archivo" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $archivo NO ENCONTRADO" -ForegroundColor Red
        $errores++
    }
}

# 2. Verificar pom.xml
Write-Host "`n📦 Verificando pom.xml..." -ForegroundColor Yellow

if (Test-Path "pom.xml") {
    $pomContent = Get-Content "pom.xml" -Raw

    # Verificar PostgreSQL
    if ($pomContent -match "postgresql") {
        Write-Host "  ✅ Dependencia PostgreSQL encontrada" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Dependencia PostgreSQL NO encontrada" -ForegroundColor Red
        $errores++
    }

    # Verificar Java version
    if ($pomContent -match "<java.version>21</java.version>") {
        Write-Host "  ✅ Java 21 configurado" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  Java version diferente de 21" -ForegroundColor Yellow
        $advertencias++
    }
}

# 3. Verificar application-prod.properties
Write-Host "`n⚙️  Verificando application-prod.properties..." -ForegroundColor Yellow

if (Test-Path "src/main/resources/application-prod.properties") {
    $prodProps = Get-Content "src/main/resources/application-prod.properties" -Raw

    if ($prodProps -match "DATABASE_URL") {
        Write-Host "  ✅ DATABASE_URL configurada" -ForegroundColor Green
    } else {
        Write-Host "  ❌ DATABASE_URL no encontrada" -ForegroundColor Red
        $errores++
    }

    if ($prodProps -match "server.port=\$\{PORT") {
        Write-Host "  ✅ Puerto dinámico configurado" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Puerto dinámico no configurado correctamente" -ForegroundColor Red
        $errores++
    }

    if ($prodProps -match "PostgreSQLDialect") {
        Write-Host "  ✅ Dialecto PostgreSQL configurado" -ForegroundColor Green
    } else {
        Write-Host "  ❌ Dialecto PostgreSQL no configurado" -ForegroundColor Red
        $errores++
    }
}

# 4. Verificar .gitignore
Write-Host "`n🚫 Verificando .gitignore..." -ForegroundColor Yellow

if (Test-Path ".gitignore") {
    $gitignore = Get-Content ".gitignore" -Raw

    if ($gitignore -match "uploads/") {
        Write-Host "  ✅ uploads/ en .gitignore" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  uploads/ no está en .gitignore" -ForegroundColor Yellow
        $advertencias++
    }

    if ($gitignore -match "\.env") {
        Write-Host "  ✅ .env en .gitignore" -ForegroundColor Green
    } else {
        Write-Host "  ⚠️  .env no está en .gitignore" -ForegroundColor Yellow
        $advertencias++
    }
}

# 5. Compilación local
Write-Host "`n🔨 Intentando compilar localmente..." -ForegroundColor Yellow

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "  Maven encontrado, compilando..." -ForegroundColor Gray

    $compileResult = & mvn clean package -DskipTests -q 2>&1

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✅ Compilación exitosa" -ForegroundColor Green

        if (Test-Path "target/sabi-0.0.1-SNAPSHOT.jar") {
            $size = (Get-Item "target/sabi-0.0.1-SNAPSHOT.jar").Length / 1MB
            Write-Host "  📦 JAR generado: $([math]::Round($size, 2)) MB" -ForegroundColor Cyan
        }
    } else {
        Write-Host "  ❌ Error en compilación" -ForegroundColor Red
        Write-Host $compileResult -ForegroundColor Red
        $errores++
    }
} else {
    Write-Host "  ⚠️  Maven no encontrado, saltando compilación" -ForegroundColor Yellow
    $advertencias++
}

# 6. Verificar estructura de directorios
Write-Host "`n📂 Verificando estructura..." -ForegroundColor Yellow

$directorios = @(
    "src/main/java",
    "src/main/resources",
    "src/main/resources/templates",
    "src/main/resources/static"
)

foreach ($dir in $directorios) {
    if (Test-Path $dir) {
        Write-Host "  ✅ $dir" -ForegroundColor Green
    } else {
        Write-Host "  ❌ $dir NO ENCONTRADO" -ForegroundColor Red
        $errores++
    }
}

# 7. Verificar Git
Write-Host "`n📚 Verificando Git..." -ForegroundColor Yellow

if (Test-Path ".git") {
    Write-Host "  ✅ Repositorio Git inicializado" -ForegroundColor Green

    $gitStatus = & git status --porcelain 2>&1
    if ($gitStatus) {
        Write-Host "  ⚠️  Hay cambios sin commit" -ForegroundColor Yellow
        $advertencias++
    } else {
        Write-Host "  ✅ Todo está commiteado" -ForegroundColor Green
    }
} else {
    Write-Host "  ❌ Git no inicializado" -ForegroundColor Red
    Write-Host "     Ejecuta: git init" -ForegroundColor Gray
    $errores++
}

# Resumen
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "📊 RESUMEN" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan

if ($errores -eq 0 -and $advertencias -eq 0) {
    Write-Host "`n✅ TODO LISTO PARA DEPLOY A RAILWAY!" -ForegroundColor Green
    Write-Host "`nPróximos pasos:" -ForegroundColor Cyan
    Write-Host "  1. git add ." -ForegroundColor Gray
    Write-Host "  2. git commit -m 'Ready for Railway deployment'" -ForegroundColor Gray
    Write-Host "  3. git push origin main" -ForegroundColor Gray
    Write-Host "  4. Configurar en Railway (ver RAILWAY_DEPLOYMENT.md)" -ForegroundColor Gray
    exit 0
} elseif ($errores -eq 0) {
    Write-Host "`n⚠️  $advertencias ADVERTENCIA(S)" -ForegroundColor Yellow
    Write-Host "El deploy debería funcionar, pero revisa las advertencias." -ForegroundColor Yellow
    exit 0
} else {
    Write-Host "`n❌ $errores ERROR(S) - $advertencias ADVERTENCIA(S)" -ForegroundColor Red
    Write-Host "Por favor, corrige los errores antes de hacer deploy." -ForegroundColor Red
    exit 1
}

