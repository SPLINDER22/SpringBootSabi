# 🔧 SOLUCIÓN: Error "undefined variable 'openjdk21'"

## ❌ ERROR EN RAILWAY:
```
error: undefined variable 'openjdk21'
at /app/.nixpacks/nixpkgs-5148520bfab61f99fd25fb9ff7bfbb50dad3c9db.nix:19:15:
```

## 🔍 CAUSA DEL PROBLEMA

Nixpacks (el sistema de build de Railway) **NO reconoce `openjdk21`** como un paquete válido.

El nombre correcto del paquete en Nix es: **`jdk21`**

## ✅ SOLUCIÓN APLICADA

He actualizado ambos archivos `nixpacks.toml`:

### Archivo 1: `nixpacks.toml` (raíz del proyecto)
```toml
[phases.setup]
nixPkgs = ['maven', 'jdk21']  ← CAMBIADO de openjdk21 a jdk21

[phases.build]
cmds = ['cd sabi && mvn clean package -DskipTests']

[start]
cmd = 'cd sabi && java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar'

[variables]
MAVEN_OPTS = '-Xmx512m'
```

### Archivo 2: `sabi/nixpacks.toml`
```toml
[phases.setup]
nixPkgs = ['maven', 'jdk21']  ← CAMBIADO de openjdk21 a jdk21

[phases.build]
cmds = ['mvn clean package -DskipTests']

[phases.start]
cmd = 'java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar'
```

## 🚀 PRÓXIMOS PASOS

### 1. Hacer commit y push
```powershell
cd C:\Users\USER\Downloads\SpringBootSabi
git add .
git commit -m "Fix: Cambiar openjdk21 a jdk21 para Nixpacks"
git push origin main
```

### 2. Railway redesplegará automáticamente
- Railway detectará el cambio en GitHub
- Iniciará un nuevo build
- Esta vez debería compilar correctamente

### 3. Verificar el nuevo deployment
1. Ve a Railway → Deployments
2. Espera a que termine el build
3. Revisa los logs para confirmar que compila

## 📊 QUÉ ESPERAR EN LOS LOGS

### ✅ Build exitoso:
```
[setup] Installing maven, jdk21
[build] cd sabi && mvn clean package -DskipTests
[INFO] BUILD SUCCESS
[INFO] Building jar: /app/sabi/target/sabi-0.0.1-SNAPSHOT.jar
```

### ✅ Inicio exitoso:
```
[start] cd sabi && java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.5.5)

Started SabiApplication in X.XXX seconds
```

## 🔍 NOMBRES DE PAQUETES JAVA EN NIXPKGS

Para futuras referencias, los nombres correctos son:

| ❌ Incorrecto | ✅ Correcto | Versión |
|---------------|-------------|---------|
| `openjdk21` | `jdk21` | Java 21 |
| `openjdk17` | `jdk17` | Java 17 |
| `openjdk11` | `jdk11` | Java 11 |
| `openjdk` | `jdk` | Última versión |

## 📝 ALTERNATIVAS (Si jdk21 tampoco funciona)

### Opción 1: Usar jdk (última versión)
```toml
[phases.setup]
nixPkgs = ['maven', 'jdk']
```

### Opción 2: Especificar versión explícita
```toml
[phases.setup]
nixPkgs = ['maven', 'temurin-bin-21']
```

### Opción 3: Usar solo Maven (incluye Java)
```toml
[phases.setup]
nixPkgs = ['maven']
```

## ⚠️ SI EL ERROR PERSISTE

Si `jdk21` tampoco funciona, prueba con:

```toml
[phases.setup]
nixPkgs = ['maven', 'temurin-bin-21']
```

O simplemente:
```toml
[phases.setup]
nixPkgs = ['maven']
```

Maven incluye una versión de Java, aunque no sea la más reciente.

## 🎯 RESUMEN

- ✅ **Problema identificado**: `openjdk21` no existe en Nixpacks
- ✅ **Solución aplicada**: Cambiar a `jdk21`
- ✅ **Archivos actualizados**: 2 archivos `nixpacks.toml`
- 🚀 **Acción requerida**: Git commit + push
- ⏳ **Resultado esperado**: Build exitoso en Railway

---

**Fecha**: 07 Diciembre 2025  
**Resuelto por**: GitHub Copilot  
**Tiempo estimado de fix**: 2-3 minutos después del push

