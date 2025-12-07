# 🚨 TROUBLESHOOTING: Application failed to respond

## 📊 SITUACIÓN ACTUAL

- ✅ Build completado exitosamente
- ✅ Base de datos PostgreSQL en línea
- ✅ Repositorio conectado
- ❌ **Aplicación no responde: "Application failed to respond"**

Esto significa que la aplicación se **compiló** pero **falló al iniciar** o está **crasheando**.

---

## 🔍 PASO 1: REVISAR LOGS EN RAILWAY

### Cómo acceder a los logs:

1. Ve a https://railway.app/dashboard
2. Haz clic en tu proyecto **SABI**
3. Haz clic en el servicio **sabi** (el servicio Spring Boot)
4. Haz clic en la pestaña **"Deployments"**
5. Haz clic en el deployment más reciente
6. Haz clic en **"View Logs"**

### Qué buscar en los logs:

#### ❌ ERRORES COMUNES:

**1. Error de Base de Datos:**
```
Failed to configure a DataSource
DATABASE_URL is not set
Connection refused
```
**Solución:** Verificar que PostgreSQL está agregado y conectado

**2. Error de Puerto:**
```
Port 8080 is already in use
Failed to bind to PORT
```
**Solución:** Railway asigna el puerto automáticamente vía `$PORT`

**3. Error de Java/Compilación:**
```
UnsupportedClassVersionError
Unsupported class file major version
```
**Solución:** Ya resuelto (cambiado a Java 17)

**4. Error de Variables de Entorno:**
```
MAIL_PASSWORD is required
Could not resolve placeholder
```
**Solución:** Configurar variables en Railway → Settings → Variables

**5. Error de Memoria:**
```
OutOfMemoryError
Java heap space
```
**Solución:** Ajustar `JAVA_OPTS` en variables de entorno

#### ✅ INICIO EXITOSO:

Si la aplicación inicia correctamente, verás:
```
Started SabiApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

---

## 🔧 PASO 2: VERIFICAR VARIABLES DE ENTORNO

### Variables REQUERIDAS:

Ve a Railway → Settings → Variables y verifica:

```
SPRING_PROFILES_ACTIVE=prod
MAIL_USERNAME=Sabi.geas5@gmail.com
MAIL_PASSWORD=Williamespinel1
UPLOAD_PATH=/app/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/app/uploads/diagnosticos
```

### Variable AUTOMÁTICA (Railway la configura):
```
DATABASE_URL=postgresql://...
```
**NO la edites manualmente** - Railway la genera automáticamente.

---

## 🔧 PASO 3: CAMBIOS APLICADOS PARA MEJORAR ESTABILIDAD

He actualizado los archivos para hacer la aplicación más robusta:

### 1. `application-prod.properties` mejorado:

✅ **Connection pool configurado** para Railway
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
```

✅ **Logs más verbosos** para debug inicial
```properties
logging.level.org.springframework.boot=INFO
logging.level.org.springframework.security=INFO
```

✅ **Correo con timeouts** para no bloquear el startup
```properties
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
```

✅ **Password de correo opcional** para no fallar si falta
```properties
spring.mail.password=${MAIL_PASSWORD:}
```

### 2. `build.sh` mejorado:

✅ Muestra variables de entorno
✅ Build verbose con logs completos
✅ Mejor manejo de errores

---

## 🚀 PASO 4: HACER COMMIT Y PUSH

```bash
cd C:\Users\USER\Downloads\SpringBootSabi
git add .
git commit -m "Fix: Mejorar configuración para Railway - Connection pool y logs"
git push origin main
```

Railway redesplegará automáticamente.

---

## 📋 PASO 5: DESPUÉS DEL REDESPLIEGUE

### 1. Espera a que termine el build (2-3 minutos)

### 2. Revisa los logs nuevamente
Busca específicamente:

**✅ Señales de éxito:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Started SabiApplication in X.XXX seconds
```

**❌ Señales de error:**
Cualquier línea con `ERROR` o `Exception` o `Failed`

### 3. Prueba el health endpoint

Una vez que los logs muestren "Started SabiApplication":

```
https://tu-dominio.railway.app/health
```

Debería responder:
```json
{
  "status": "UP",
  "application": "Sabi",
  "timestamp": "..."
}
```

---

## 🔍 PASO 6: DIAGNÓSTICOS ESPECÍFICOS

### Si sigue sin funcionar, necesito ver LOS LOGS COMPLETOS

**Copia y pégame:**
1. Las últimas 100 líneas de los logs
2. Cualquier mensaje de ERROR
3. El mensaje exacto cuando falla

### Comandos útiles en Railway:

**Ver logs en tiempo real:**
- En Railway → Deployments → View Logs
- Se actualizan automáticamente

**Reiniciar el servicio:**
- Railway → Settings → Restart Deployment

**Ver métricas:**
- Railway → Metrics
- CPU, Memoria, Requests

---

## 🎯 POSIBLES SOLUCIONES SEGÚN EL ERROR

### ERROR: Cannot create DataSource
```bash
# Verificar que PostgreSQL está agregado
Railway → + New → Database → Add PostgreSQL
```

### ERROR: Port already in use
```properties
# Verificar que server.port usa $PORT
server.port=${PORT:8080}
```
✅ Ya está configurado correctamente

### ERROR: Mail configuration failed
```bash
# Agregar password de correo o usar App Password de Gmail
MAIL_PASSWORD=tu-app-password-de-gmail
```

### ERROR: OutOfMemoryError
```bash
# Aumentar memoria Java
JAVA_OPTS=-Xmx768m -Xms256m
```

### ERROR: Failed to bind to 0.0.0.0:8080
```bash
# Verificar que no hay otro servicio en el mismo puerto
# Railway maneja esto automáticamente, no debería pasar
```

---

## 📞 PRÓXIMO PASO

**POR FAVOR, DESPUÉS DE HACER EL PUSH:**

1. Ve a Railway → Deployments → View Logs
2. Espera a que aparezca "Started SabiApplication" o un error
3. **Cópiame el error COMPLETO** si hay alguno
4. Incluye al menos las últimas 50-100 líneas de los logs

Con esa información podré darte la solución exacta.

---

## ✅ CHECKLIST RÁPIDO

Antes de contactarme con logs:

- [ ] PostgreSQL está agregado en Railway
- [ ] Variables de entorno configuradas (al menos `SPRING_PROFILES_ACTIVE=prod`)
- [ ] Último commit incluye los cambios de Java 17
- [ ] Deployment terminó (no está en "Building...")
- [ ] Revisé los logs completos (View Logs)
- [ ] Copié el mensaje de error específico

---

**Fecha:** 07 Diciembre 2025  
**Estado:** Configuración mejorada - Pendiente de redespliegue  
**Acción requerida:** Git push + Revisar logs

