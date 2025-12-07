# 🚀 GUÍA DE DESPLIEGUE EN RAILWAY
# Autor: GitHub Copilot
# Fecha: Diciembre 2024

## 📋 PRE-REQUISITOS

1. **Cuenta en Railway**: https://railway.app
2. **GitHub Account**: Para conectar tu repositorio
3. **Git instalado**: Para subir el código

---

## 🔧 PASO 1: PREPARAR EL PROYECTO

### 1.1 Verificar archivos creados
Asegúrate de que existen estos archivos:
- ✅ `Procfile`
- ✅ `railway.json`
- ✅ `nixpacks.toml`
- ✅ `application-prod.properties`
- ✅ `.gitignore`

### 1.2 Compilar localmente (opcional)
```bash
mvn clean package -DskipTests
```

---

## 📤 PASO 2: SUBIR A GITHUB

### 2.1 Inicializar repositorio Git (si no existe)
```bash
cd sabi
git init
git add .
git commit -m "Initial commit - Sabi App ready for Railway"
```

### 2.2 Crear repositorio en GitHub
1. Ve a https://github.com/new
2. Nombre: `sabi-app` (o el que prefieras)
3. **NO** inicialices con README
4. Crea el repositorio

### 2.3 Conectar y subir
```bash
git remote add origin https://github.com/TU_USUARIO/sabi-app.git
git branch -M main
git push -u origin main
```

---

## 🚂 PASO 3: DESPLEGAR EN RAILWAY

### 3.1 Crear nuevo proyecto
1. Ve a https://railway.app/dashboard
2. Click en **"New Project"**
3. Selecciona **"Deploy from GitHub repo"**
4. Autoriza Railway si es necesario
5. Selecciona tu repositorio `sabi-app`

### 3.2 Agregar base de datos PostgreSQL
1. En tu proyecto, click **"New"** → **"Database"** → **"Add PostgreSQL"**
2. Railway automáticamente creará la variable `DATABASE_URL`

### 3.3 Configurar variables de entorno
En **Settings** → **Variables**, agrega:

```env
# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Java Options
JAVA_OPTS=-Xmx512m -Xms256m

# Email Configuration (IMPORTANTE: Usa tus credenciales reales)
MAIL_USERNAME=Sabi.geas5@gmail.com
MAIL_PASSWORD=Williamespinel1

# Upload Paths (Railway usa sistema de archivos efímero)
UPLOAD_PATH=/tmp/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/tmp/uploads/diagnosticos

# Puerto (Railway lo asigna automáticamente, pero puedes especificarlo)
PORT=8080
```

⚠️ **NOTA IMPORTANTE SOBRE ARCHIVOS**:
Railway usa almacenamiento **efímero** en `/tmp`. Los archivos se borrarán al reiniciar.

**Soluciones recomendadas:**
- **Cloudinary** (imágenes): https://cloudinary.com (plan gratuito)
- **AWS S3** (archivos): https://aws.amazon.com/s3/
- **Railway Volumes** (persistencia): Ver documentación

### 3.4 Configurar dominio (opcional)
1. Railway genera un dominio automático: `tu-app.up.railway.app`
2. Para dominio personalizado: **Settings** → **Domains** → **Custom Domain**

---

## 🔍 PASO 4: VERIFICAR DESPLIEGUE

### 4.1 Ver logs
1. En Railway dashboard, click en tu servicio
2. Ve a la pestaña **"Deployments"**
3. Click en el deployment activo
4. Ve a **"View Logs"**

### 4.2 Verificar salud de la app
Busca en los logs:
```
✅ Started SabiApplication in X.XXX seconds
✅ Directorio de perfiles creado
✅ Directorio de diagnósticos creado
✅ Directorio de certificaciones creado
```

### 4.3 Acceder a la aplicación
```
https://tu-proyecto.up.railway.app
```

---

## ⚠️ PROBLEMAS COMUNES Y SOLUCIONES

### 1. Error de conexión a base de datos
**Síntoma**: `Connection refused` o `Unknown database`
**Solución**: 
- Verifica que PostgreSQL esté añadido
- Verifica que `DATABASE_URL` esté en variables de entorno
- Railway la crea automáticamente

### 2. Error de memoria
**Síntoma**: `OutOfMemoryError`
**Solución**: Ajusta `JAVA_OPTS`:
```
JAVA_OPTS=-Xmx768m -Xms384m
```

### 3. Archivos no se guardan
**Síntoma**: Imágenes/PDFs desaparecen después de reiniciar
**Solución**: 
- Es normal en Railway (almacenamiento efímero)
- Implementa Cloudinary o S3 (ver sección siguiente)

### 4. Error al compilar
**Síntoma**: Build falla en Railway
**Solución**:
- Verifica que `pom.xml` esté correcto
- Intenta compilar localmente: `mvn clean package`
- Revisa logs de Railway para ver el error específico

### 5. Puerto incorrecto
**Síntoma**: Application timeout
**Solución**: 
- Asegúrate que `application-prod.properties` tenga:
  ```
  server.port=${PORT:8080}
  ```

---

## 🎯 PASO 5: OPTIMIZACIONES RECOMENDADAS

### 5.1 Implementar almacenamiento en la nube

#### Cloudinary (Imágenes de perfil)
```xml
<!-- En pom.xml -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.36.0</version>
</dependency>
```

Variables de entorno:
```
CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
```

#### AWS S3 (Certificaciones y diagnósticos)
```xml
<!-- En pom.xml -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.0</version>
</dependency>
```

Variables de entorno:
```
AWS_ACCESS_KEY_ID=tu_key
AWS_SECRET_ACCESS_KEY=tu_secret
AWS_REGION=us-east-1
S3_BUCKET_NAME=sabi-uploads
```

### 5.2 Configurar health check
Crea endpoint en Spring Boot:
```java
@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
```

En Railway: **Settings** → **Health Check Path**: `/health`

### 5.3 Configurar monitoreo
Railway proporciona métricas automáticas en **Observability**

---

## 💰 COSTOS ESTIMADOS

### Railway Pricing (Diciembre 2024)

**Plan Starter (Gratis)**:
- $5 USD de crédito gratis/mes
- Hasta 500 horas de ejecución
- 512 MB RAM
- 1 GB almacenamiento
- **Ideal para comenzar**

**Plan Developer ($20/mes)**:
- Hasta 500 horas de ejecución
- 8 GB RAM por servicio
- 100 GB almacenamiento
- Mejor para producción

**Consumo estimado de Sabi:**
- App Spring Boot: ~$5-10/mes
- PostgreSQL: ~$5/mes
- **Total: $10-15/mes** (puede usar plan gratuito inicialmente)

### Cloudinary Pricing
**Plan Free**:
- 25 créditos/mes
- 25 GB almacenamiento
- 25 GB bandwidth
- **Suficiente para empezar**

### AWS S3 Pricing
**Plan Free (12 meses)**:
- 5 GB almacenamiento
- 20,000 solicitudes GET
- 2,000 solicitudes PUT
- **Suficiente para desarrollo**

---

## 🔐 SEGURIDAD

### 1. Cambiar credenciales de correo
**IMPORTANTE**: La contraseña en el código es visible. Debes:
1. Crear una nueva "App Password" en Google
2. Guardarla en Railway como variable de entorno
3. **NO** subirla a GitHub

### 2. Configurar HTTPS
Railway proporciona HTTPS automáticamente ✅

### 3. Variables de entorno sensibles
**NUNCA** subas a GitHub:
- Contraseñas
- API Keys
- Tokens
Usa variables de entorno en Railway

---

## 📊 MONITOREO POST-DESPLIEGUE

### Logs en tiempo real
```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Ver logs
railway logs
```

### Métricas a vigilar
1. **CPU Usage**: Debe estar < 70%
2. **Memory Usage**: Debe estar < 80%
3. **Response Time**: Debe ser < 2 segundos
4. **Error Rate**: Debe ser < 1%

---

## 🆘 SOPORTE

Si tienes problemas:
1. **Railway Discord**: https://discord.gg/railway
2. **Railway Docs**: https://docs.railway.app
3. **Spring Boot Docs**: https://spring.io/guides

---

## ✅ CHECKLIST FINAL

Antes de considerar completo:

- [ ] App se despliega sin errores
- [ ] Base de datos PostgreSQL conectada
- [ ] Puedes crear usuario y login
- [ ] Puedes subir imagen de perfil
- [ ] Entrenadores pueden subir certificaciones
- [ ] Clientes pueden subir diagnósticos
- [ ] Correos se envían correctamente
- [ ] HTTPS funciona
- [ ] Dominio configurado (opcional)
- [ ] Cloudinary/S3 implementado (recomendado)

---

## 🎉 ¡LISTO!

Tu aplicación Sabi está ahora en producción en Railway.

**Próximos pasos:**
1. Implementar almacenamiento en la nube (Cloudinary/S3)
2. Configurar dominio personalizado
3. Agregar Google Analytics
4. Implementar cache con Redis (opcional)
5. Configurar CDN para assets estáticos

---

**¿Preguntas?** Revisa la documentación o contacta soporte.

