# Guía de Despliegue en Railway - SABI

## 📋 Requisitos Previos

Antes de desplegar en Railway, asegúrate de tener:
- Una cuenta en Railway (https://railway.app)
- El repositorio de GitHub conectado a Railway
- Variables de entorno configuradas

## 🚀 Pasos para Desplegar

### 1. Crear un Nuevo Proyecto en Railway

1. Ve a https://railway.app y haz clic en "New Project"
2. Selecciona "Deploy from GitHub repo"
3. Elige tu repositorio de GitHub
4. Railway detectará automáticamente que es un proyecto Java con Maven

### 2. Configurar Variables de Entorno

En el panel de Railway, ve a la pestaña "Variables" y agrega las siguientes:

#### Variables Obligatorias:

```bash
# Base de Datos (Railway proporciona DATABASE_URL automáticamente si agregas PostgreSQL)
DATABASE_URL=<se configura automáticamente con PostgreSQL>

# Correo Electrónico
MAIL_USERNAME=Sabi.geas5@gmail.com
MAIL_PASSWORD=<tu-contraseña-de-aplicación-de-gmail>

# Rutas de Archivos
UPLOAD_PATH=/app/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/app/uploads/diagnosticos

# Perfil de Spring
SPRING_PROFILES_ACTIVE=prod

# Puerto (Railway lo proporciona automáticamente)
PORT=<railway lo asigna automáticamente>
```

#### Variables Opcionales:

```bash
# Java Options
JAVA_OPTS=-Xmx512m -Xms256m

# Maven Options
MAVEN_OPTS=-Xmx512m
```

### 3. Agregar Base de Datos PostgreSQL

1. En tu proyecto de Railway, haz clic en "+ New"
2. Selecciona "Database" → "Add PostgreSQL"
3. Railway creará automáticamente la base de datos y configurará DATABASE_URL
4. **IMPORTANTE**: Conecta la base de datos con tu servicio

### 4. Configurar el Dominio

1. Ve a la pestaña "Settings" de tu servicio
2. En "Networking", haz clic en "Generate Domain"
3. Railway te proporcionará un dominio público (ej: `tu-app.up.railway.app`)

### 5. Desplegar

Railway desplegará automáticamente tu aplicación cuando:
- Hagas push a tu rama principal (main/master)
- Hagas cambios en las variables de entorno
- Hagas clic en "Deploy" manualmente

## 🔧 Configuración de Archivos

El proyecto incluye los siguientes archivos de configuración para Railway:

### `nixpacks.toml` (en la raíz)
```toml
[phases.setup]
nixPkgs = ['maven', 'openjdk21']

[phases.build]
cmds = ['cd sabi && mvn clean package -DskipTests']

[phases.start]
cmd = 'cd sabi && java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar'

[variables]
MAVEN_OPTS = '-Xmx512m'
```

### `railway.toml` (en la raíz)
```toml
[build]
builder = "NIXPACKS"
watchPatterns = ["sabi/**"]

[deploy]
numReplicas = 1
restartPolicyType = "ON_FAILURE"
restartPolicyMaxRetries = 10
```

## 📝 Configuración de Gmail para Correos

1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. Activa la verificación en dos pasos
3. Genera una "Contraseña de aplicación":
   - Ve a Seguridad → Verificación en dos pasos → Contraseñas de aplicaciones
   - Selecciona "Correo" y "Otro (nombre personalizado)"
   - Escribe "SABI Railway"
   - Copia la contraseña generada
4. Usa esta contraseña en la variable `MAIL_PASSWORD`

## 🗂️ Persistencia de Archivos

**IMPORTANTE**: Railway usa almacenamiento efímero. Los archivos subidos se perderán si el contenedor se reinicia.

### Soluciones recomendadas:

#### Opción 1: Cloudinary (Recomendado)
```bash
# Agregar estas variables de entorno
CLOUDINARY_CLOUD_NAME=tu-cloud-name
CLOUDINARY_API_KEY=tu-api-key
CLOUDINARY_API_SECRET=tu-api-secret
CLOUDINARY_ENABLED=true
```

#### Opción 2: AWS S3
- Configura un bucket en AWS S3
- Actualiza la aplicación para usar S3 en lugar de almacenamiento local

#### Opción 3: Railway Volumes (Beta)
- Railway está trabajando en soporte para volúmenes persistentes
- Consulta la documentación actualizada de Railway

## 🔍 Verificación del Despliegue

### 1. Verificar que la aplicación está corriendo:
```bash
curl https://tu-app.up.railway.app/
```

### 2. Verificar la base de datos:
- Accede a los logs en Railway
- Busca mensajes como "Started SabiApplication"
- Verifica que no haya errores de conexión a la base de datos

### 3. Probar funcionalidades clave:
- Registro de usuarios
- Login
- Subida de archivos (aunque serán temporales)
- Envío de correos

## 🐛 Solución de Problemas Comunes

### Error: "No se puede conectar a la base de datos"
- Verifica que DATABASE_URL esté configurada
- Asegúrate de que el servicio PostgreSQL esté corriendo
- Verifica que ambos servicios estén en el mismo proyecto

### Error: "Failed to send email"
- Verifica MAIL_USERNAME y MAIL_PASSWORD
- Asegúrate de usar una contraseña de aplicación de Gmail
- Verifica que la verificación en dos pasos esté activa

### Error: "OutOfMemoryError"
- Aumenta JAVA_OPTS a `-Xmx1024m`
- Considera actualizar tu plan de Railway para más RAM

### Error: "Application failed to start"
- Revisa los logs en Railway
- Verifica que todas las variables de entorno estén configuradas
- Asegúrate de que Java 21 esté disponible

## 📊 Monitoreo

Railway proporciona:
- Logs en tiempo real
- Métricas de CPU y memoria
- Reinicio automático en caso de fallas

## 💰 Costos

Railway ofrece:
- **Plan Developer (Gratis)**: $5 de crédito mensual (~500 horas)
- **Plan Hobby**: $5/mes por uso
- **Plan Pro**: $20/mes con más recursos

## 🔄 Actualizaciones

Para actualizar la aplicación:
1. Haz cambios en tu código local
2. Commit y push a GitHub
3. Railway detectará los cambios y desplegará automáticamente

## 📞 Soporte

- Documentación de Railway: https://docs.railway.app
- Comunidad de Railway: https://discord.gg/railway
- Issues del proyecto: [Tu repositorio de GitHub]

## ✅ Checklist de Despliegue

- [ ] Cuenta de Railway creada
- [ ] Repositorio conectado
- [ ] PostgreSQL agregado y conectado
- [ ] Variables de entorno configuradas
- [ ] Contraseña de aplicación de Gmail generada
- [ ] Dominio generado
- [ ] Primera compilación exitosa
- [ ] Aplicación accesible vía dominio
- [ ] Login funciona correctamente
- [ ] Registro de usuarios funciona
- [ ] Correos se envían correctamente
- [ ] Plan de almacenamiento de archivos decidido

## 🎯 Próximos Pasos

1. **Implementar almacenamiento permanente** (Cloudinary o S3)
2. **Configurar dominio personalizado** (si lo tienes)
3. **Configurar backups de base de datos**
4. **Implementar monitoreo avanzado**
5. **Optimizar rendimiento**

