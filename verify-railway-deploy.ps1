# Script de Verificación Pre-Despliegue para Railway
# Ejecutar antes de hacer push a Railway

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  SABI - Verificación Pre-Despliegue" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$errors = 0
$warnings = 0

# Verificar estructura del proyecto
Write-Host "[1/10] Verificando estructura del proyecto..." -ForegroundColor Yellow
if (Test-Path ".\sabi\pom.xml") {
    Write-Host "  ✓ pom.xml encontrado" -ForegroundColor Green
} else {
    Write-Host "  ✗ pom.xml NO encontrado" -ForegroundColor Red
    $errors++
}

if (Test-Path ".\nixpacks.toml") {
    Write-Host "  ✓ nixpacks.toml encontrado en raíz" -ForegroundColor Green
} else {
    Write-Host "  ✗ nixpacks.toml NO encontrado en raíz" -ForegroundColor Red
    $errors++
}

if (Test-Path ".\railway.toml") {
    Write-Host "  ✓ railway.toml encontrado" -ForegroundColor Green
} else {
    Write-Host "  ✗ railway.toml NO encontrado" -ForegroundColor Red
    $errors++
}

# Verificar archivos de configuración
Write-Host ""
Write-Host "[2/10] Verificando archivos de configuración..." -ForegroundColor Yellow
if (Test-Path ".\sabi\src\main\resources\application-prod.properties") {
    Write-Host "  ✓ application-prod.properties encontrado" -ForegroundColor Green

    # Verificar contenido
    $prodProps = Get-Content ".\sabi\src\main\resources\application-prod.properties" -Raw

    if ($prodProps -match 'DATABASE_URL') {
        Write-Host "  ✓ DATABASE_URL configurado" -ForegroundColor Green
    } else {
        Write-Host "  ✗ DATABASE_URL NO configurado" -ForegroundColor Red
        $errors++
    }

    if ($prodProps -match 'spring.mail') {
        Write-Host "  ✓ Configuración de correo presente" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ Configuración de correo NO encontrada" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ✗ application-prod.properties NO encontrado" -ForegroundColor Red
    $errors++
}

# Verificar dependencias
Write-Host ""
Write-Host "[3/10] Verificando dependencias en pom.xml..." -ForegroundColor Yellow
$pomContent = Get-Content ".\sabi\pom.xml" -Raw

if ($pomContent -match 'postgresql') {
    Write-Host "  ✓ Driver PostgreSQL incluido" -ForegroundColor Green
} else {
    Write-Host "  ✗ Driver PostgreSQL NO incluido" -ForegroundColor Red
    $errors++
}

if ($pomContent -match 'spring-boot-starter-web') {
    Write-Host "  ✓ Spring Boot Web incluido" -ForegroundColor Green
} else {
    Write-Host "  ✗ Spring Boot Web NO incluido" -ForegroundColor Red
    $errors++
}

# Verificar versión de Java
Write-Host ""
Write-Host "[4/10] Verificando configuración de Java..." -ForegroundColor Yellow
if ($pomContent -match '<java.version>21</java.version>') {
    Write-Host "  ✓ Java 21 configurado en pom.xml" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Java 21 NO configurado (verificar versión)" -ForegroundColor Yellow
    $warnings++
}

# Verificar que la aplicación compile localmente
Write-Host ""
Write-Host "[5/10] Verificando compilación local..." -ForegroundColor Yellow
Write-Host "  Ejecutando: mvn clean package -DskipTests..." -ForegroundColor Gray

Push-Location ".\sabi"
try {
    $buildOutput = & mvn clean package -DskipTests 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  ✓ Compilación exitosa" -ForegroundColor Green

        if (Test-Path ".\target\sabi-0.0.1-SNAPSHOT.jar") {
            Write-Host "  ✓ JAR generado correctamente" -ForegroundColor Green
        } else {
            Write-Host "  ✗ JAR NO encontrado" -ForegroundColor Red
            $errors++
        }
    } else {
        Write-Host "  ✗ Error en la compilación" -ForegroundColor Red
        $errors++
    }
} catch {
    Write-Host "  ✗ No se pudo ejecutar Maven (¿está instalado?)" -ForegroundColor Red
    Write-Host "  Mensaje: $_" -ForegroundColor Red
    $warnings++
}
Pop-Location

# Verificar archivos estáticos
Write-Host ""
Write-Host "[6/10] Verificando recursos estáticos..." -ForegroundColor Yellow
if (Test-Path ".\sabi\src\main\resources\static") {
    $cssFiles = Get-ChildItem ".\sabi\src\main\resources\static\css" -ErrorAction SilentlyContinue
    $jsFiles = Get-ChildItem ".\sabi\src\main\resources\static\js" -ErrorAction SilentlyContinue

    if ($cssFiles) {
        Write-Host "  ✓ Archivos CSS encontrados ($($cssFiles.Count) archivos)" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ No se encontraron archivos CSS" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ⚠ Carpeta static NO encontrada" -ForegroundColor Yellow
    $warnings++
}

# Verificar templates
Write-Host ""
Write-Host "[7/10] Verificando templates..." -ForegroundColor Yellow
if (Test-Path ".\sabi\src\main\resources\templates") {
    $htmlFiles = Get-ChildItem ".\sabi\src\main\resources\templates" -Recurse -Filter "*.html" -ErrorAction SilentlyContinue

    if ($htmlFiles) {
        Write-Host "  ✓ Templates HTML encontrados ($($htmlFiles.Count) archivos)" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ No se encontraron templates HTML" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ✗ Carpeta templates NO encontrada" -ForegroundColor Red
    $errors++
}

# Verificar git
Write-Host ""
Write-Host "[8/10] Verificando Git..." -ForegroundColor Yellow
if (Test-Path ".\.git") {
    Write-Host "  ✓ Repositorio Git inicializado" -ForegroundColor Green

    try {
        $gitRemote = git remote -v 2>&1
        if ($gitRemote -match 'origin') {
            Write-Host "  ✓ Remote 'origin' configurado" -ForegroundColor Green
        } else {
            Write-Host "  ⚠ Remote 'origin' NO configurado" -ForegroundColor Yellow
            $warnings++
        }
    } catch {
        Write-Host "  ⚠ No se pudo verificar remotes de Git" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ✗ NO es un repositorio Git" -ForegroundColor Red
    $errors++
}

# Verificar .gitignore
Write-Host ""
Write-Host "[9/10] Verificando .gitignore..." -ForegroundColor Yellow
if (Test-Path ".\.gitignore") {
    Write-Host "  ✓ .gitignore encontrado" -ForegroundColor Green

    $gitignoreContent = Get-Content ".\.gitignore" -Raw
    if ($gitignoreContent -match 'target/') {
        Write-Host "  ✓ target/ excluido" -ForegroundColor Green
    } else {
        Write-Host "  ⚠ target/ NO excluido (agregar a .gitignore)" -ForegroundColor Yellow
        $warnings++
    }
} else {
    Write-Host "  ⚠ .gitignore NO encontrado" -ForegroundColor Yellow
    $warnings++
}

# Verificar tamaño del proyecto
Write-Host ""
Write-Host "[10/10] Verificando tamaño del proyecto..." -ForegroundColor Yellow
$projectSize = (Get-ChildItem -Path ".\sabi" -Recurse -ErrorAction SilentlyContinue |
                Measure-Object -Property Length -Sum).Sum / 1MB
$projectSize = [math]::Round($projectSize, 2)

Write-Host "  Tamaño del proyecto: $projectSize MB" -ForegroundColor Gray
if ($projectSize -lt 500) {
    Write-Host "  ✓ Tamaño aceptable para Railway" -ForegroundColor Green
} else {
    Write-Host "  ⚠ Proyecto grande, puede tardar en compilar" -ForegroundColor Yellow
    $warnings++
}

# Resumen
Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  RESUMEN DE VERIFICACIÓN" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

if ($errors -eq 0 -and $warnings -eq 0) {
    Write-Host "  ✓ TODO PERFECTO - Listo para desplegar" -ForegroundColor Green
    Write-Host ""
    Write-Host "Pasos siguientes:" -ForegroundColor Cyan
    Write-Host "1. Haz commit de tus cambios: git add . && git commit -m 'Preparar para Railway'" -ForegroundColor White
    Write-Host "2. Haz push a tu repositorio: git push origin main" -ForegroundColor White
    Write-Host "3. Conecta tu repo en Railway: https://railway.app/new" -ForegroundColor White
    Write-Host "4. Agrega PostgreSQL en Railway" -ForegroundColor White
    Write-Host "5. Configura las variables de entorno" -ForegroundColor White
    Write-Host ""
} elseif ($errors -eq 0) {
    Write-Host "  ⚠ $warnings advertencia(s) encontrada(s)" -ForegroundColor Yellow
    Write-Host "  Puedes continuar, pero revisa las advertencias" -ForegroundColor Yellow
} else {
    Write-Host "  ✗ $errors error(es) y $warnings advertencia(s) encontrados" -ForegroundColor Red
    Write-Host "  Por favor, corrige los errores antes de desplegar" -ForegroundColor Red
}

Write-Host ""
Write-Host "Para más información, lee: RAILWAY_DEPLOYMENT_GUIDE.md" -ForegroundColor Cyan
Write-Host ""

# Pausa para que el usuario pueda leer
Read-Host "Presiona Enter para continuar"

