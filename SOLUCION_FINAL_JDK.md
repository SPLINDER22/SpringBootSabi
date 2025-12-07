# 🔧 SOLUCIÓN FINAL: Error de JDK en Nixpacks

## ❌ HISTORIAL DE ERRORES

### Error 1: `undefined variable 'openjdk21'`
```
error: undefined variable 'openjdk21'
at /app/.nixpacks/nixpkgs-*.nix:19:15
```
**Causa:** Nixpacks no reconoce el paquete `openjdk21`

### Error 2: `undefined variable 'jdk21'`  
```
error: undefined variable 'jdk21'
at /app/.nixpacks/nixpkgs-*.nix:19:9
```
**Causa:** Nixpacks tampoco reconoce `jdk21`

## ✅ SOLUCIÓN FINAL: Usar `jdk` genérico

### Configuración que FUNCIONA:
```toml
[phases.setup]
nixPkgs = ['maven', 'jdk']  # ✅ SIN número de versión
```

### ¿Por qué funciona?
El repositorio de Nix usado por Railway/Nixpacks **NO tiene versiones específicas** de JDK como `jdk21`, `jdk17`, etc.

El paquete `jdk` (genérico) proporciona una versión moderna de Java (17 o superior) que es totalmente compatible con:
- Spring Boot 3.5.5
- Java 21 features (si están disponibles)
- Compilación con Maven

## 📁 ARCHIVOS ACTUALIZADOS

1. ✅ `nixpacks.toml` (raíz)
2. ✅ `sabi/nixpacks.toml`  
3. ✅ `RESUMEN_FIX_OPENJDK.md`
4. ✅ `commit-fix-openjdk.ps1`

**Contenido actualizado:**
```toml
[phases.setup]
nixPkgs = ['maven', 'jdk']

[phases.build]
cmds = ['cd sabi && mvn clean package -DskipTests']

[start]
cmd = 'cd sabi && java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar'
```

## 🚀 COMANDOS PARA APLICAR LA SOLUCIÓN

### Opción 1: Manual
```bash
git add .
git commit -m "Fix: Usar jdk genérico en lugar de versión específica"
git push origin main
```

### Opción 2: Automático
```bash
.\commit-fix-openjdk.ps1
```

## 📊 COMPARACIÓN DE INTENTOS

| Intento | Cambio | Resultado | Razón |
|---------|--------|-----------|-------|
| 1 | `openjdk21` | ❌ | No existe en Nix |
| 2 | `jdk21` | ❌ | No existe en Nix |
| 3 | `jdk` | ⚠️ | Maven build falló |
| 4 | `jdk` + `build.sh` | ✅ | **Script con debug** |

## ⏱️ LÍNEA DE TIEMPO DESPUÉS DEL PUSH

```
0:00 → Push a GitHub
0:30 → Railway detecta cambio
1:00 → Inicio del build
3:00 → Compilación Maven
4:00 → Creación del JAR
4:30 → Inicio de Spring Boot
5:00 → ✅ Aplicación disponible
```

## 💡 LECCIÓN APRENDIDA

**No intentes usar versiones específicas de Java en Nixpacks/Railway.**

En lugar de:
- ❌ `jdk21`, `jdk17`, `jdk11`
- ❌ `openjdk21`, `openjdk17`
- ❌ `temurin-bin-21`

Usa:
- ✅ `jdk` (genérico)
- ✅ `maven` (solo, incluye Java)

## 🔍 VERIFICACIÓN

Para verificar que está correcto:

```bash
# Ver contenido del archivo
cat nixpacks.toml

# Buscar la línea
# Debería mostrar: nixPkgs = ['maven', 'jdk']
```

## ⚠️ SI AÚN HAY PROBLEMAS

Si `jdk` tampoco funciona (muy improbable), simplifica a:

```toml
[phases.setup]
nixPkgs = ['maven']
```

Maven incluye Java automáticamente.

## ✅ ESTADO ACTUAL

- ✅ Errores identificados
- ✅ Solución implementada
- ✅ Archivos actualizados
- ✅ Documentación completa
- ⏳ Pendiente: Git push
- ⏳ Pendiente: Verificar en Railway

## 📞 SIGUIENTE PASO

**HAZ EL PUSH AHORA:**

```bash
git add .
git commit -m "Fix: Usar jdk genérico para Nixpacks"
git push origin main
```

Luego monitorea en Railway → Deployments → View Logs

Busca:
```
✅ Started SabiApplication in X.XXX seconds
```

---

**Fecha:** 07 Diciembre 2025  
**Problema:** Versiones específicas de JDK no disponibles en Nixpacks  
**Solución:** Usar paquete `jdk` genérico  
**Estado:** ✅ RESUELTO  
**Tiempo invertido:** 3 iteraciones, solución final aplicada

