# 🔐 VARIABLES DE ENTORNO PARA RAILWAY

## 📋 Variables Obligatorias

Configura estas variables en Railway Dashboard → Settings → Variables:

### 1. Spring Profile
```bash
SPRING_PROFILES_ACTIVE=prod
```
**Descripción**: Activa el perfil de producción que usa PostgreSQL.

---

### 2. Java Options
```bash
JAVA_OPTS=-Xmx512m -Xms256m
```
**Descripción**: Configura memoria para JVM.
- **-Xmx512m**: Máximo 512MB de RAM (ajustar según plan Railway)
- **-Xms256m**: Inicial 256MB de RAM

**Ajustes según plan**:
- Plan Free/Starter: `-Xmx400m -Xms200m`
- Plan Developer: `-Xmx768m -Xms384m`

---

### 3. Correo Electrónico

```bash
MAIL_USERNAME=sabi.gaes5@gmail.com
MAIL_PASSWORD=zkyl zvnv gknn riyt
```

**⚠️ IMPORTANTE**: 
- Debes crear una **App Password** nueva en tu cuenta Google
- No uses tu contraseña normal de Gmail
- Pasos:
  1. Ve a https://myaccount.google.com/security
  2. Activa verificación en 2 pasos
  3. Genera "App Password"
  4. Usa esa contraseña aquí

---

### 4. Rutas de Uploads

```bash
UPLOAD_PATH=/tmp/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/tmp/uploads/diagnosticos
```

**Descripción**: Directorios para archivos subidos.

**⚠️ NOTA**: Railway usa almacenamiento efímero. Los archivos se borran al reiniciar.

**Solución permanente**: Ver sección Cloudinary/S3 más abajo.

---

## 🗄️ Variables Automáticas (Railway las crea)

Railway crea estas automáticamente al añadir PostgreSQL:

```bash
DATABASE_URL=postgresql://user:pass@host:port/dbname
DATABASE_PRIVATE_URL=postgresql://...
```

**No necesitas configurarlas manualmente.**

---

## 🔧 Variables Opcionales

### Puerto (generalmente no necesario)
```bash
PORT=8080
```
Railway lo asigna automáticamente. Solo configúralo si tienes problemas.

---

### Zona Horaria
```bash
TZ=America/Bogota
```
**Descripción**: Zona horaria para logs y timestamps.

---

### Nivel de Logs
```bash
LOG_LEVEL=INFO
```
**Opciones**: DEBUG, INFO, WARN, ERROR

---

## 💾 Almacenamiento Persistente (Cloudinary)

Si implementas Cloudinary para imágenes:

```bash
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

O simplemente:
```bash
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name
```

**Obtener credenciales**:
1. Regístrate en https://cloudinary.com (plan gratuito)
2. Dashboard → Account Details
3. Copia API Environment variable

---

## 📦 Almacenamiento Persistente (AWS S3)

Si implementas AWS S3 para PDFs/documentos:

```bash
AWS_ACCESS_KEY_ID=tu_access_key
AWS_SECRET_ACCESS_KEY=tu_secret_key
AWS_REGION=us-east-1
S3_BUCKET_NAME=sabi-uploads
```

**Obtener credenciales**:
1. Consola AWS → IAM → Users → Create User
2. Adjuntar política: `AmazonS3FullAccess`
3. Generar Access Keys

---

## 🔒 Seguridad Adicional (Opcional)

### JWT Secret (si implementas autenticación JWT)
```bash
JWT_SECRET=tu_secreto_muy_largo_y_aleatorio_aqui
```

### Session Secret
```bash
SESSION_SECRET=otro_secreto_muy_largo_aqui
```

---

## 📝 Plantilla Completa para Railway

Copia y pega en Railway → Variables:

```bash
# === OBLIGATORIAS ===
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xmx512m -Xms256m
MAIL_USERNAME=sabi.gaes5@gmail.com
MAIL_PASSWORD=zkyl zvnv gknn riyt
UPLOAD_PATH=/tmp/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/tmp/uploads/diagnosticos

# === OPCIONALES ===
TZ=America/Bogota
LOG_LEVEL=INFO

# === CLOUDINARY (si lo usas) ===
# CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name

# === AWS S3 (si lo usas) ===
# AWS_ACCESS_KEY_ID=tu_key
# AWS_SECRET_ACCESS_KEY=tu_secret
# AWS_REGION=us-east-1
# S3_BUCKET_NAME=sabi-uploads
```

---

## 🔍 Verificar Variables en Railway

### Desde Dashboard:
1. Railway Dashboard → Tu Proyecto
2. Click en tu servicio
3. Settings → Variables
4. Verifica que todas estén presentes

### Desde Railway CLI:
```bash
railway login
railway variables
```

---

## ⚠️ Problemas Comunes

### 1. Error: "DATABASE_URL not set"
**Causa**: PostgreSQL no está añadido

**Solución**:
- Railway → New → Database → Add PostgreSQL
- Railway crea DATABASE_URL automáticamente

---

### 2. Error: "Authentication failed for user"
**Causa**: DATABASE_URL incorrecta

**Solución**:
- **NO** edites DATABASE_URL manualmente
- Elimina y vuelve a añadir PostgreSQL
- Railway regenerará las credenciales

---

### 3. Error: "Mail server connection failed"
**Causa**: MAIL_PASSWORD incorrecta

**Solución**:
- Genera nueva App Password en Google
- Actualiza MAIL_PASSWORD en Railway
- Redespliega

---

### 4. App usa mucha RAM
**Causa**: JAVA_OPTS muy alto

**Solución**:
```bash
# Reducir memoria:
JAVA_OPTS=-Xmx400m -Xms200m

# O actualizar plan Railway
```

---

## 📚 Referencias

- **Railway Docs**: https://docs.railway.app/develop/variables
- **Spring Boot Externalized Config**: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config
- **12 Factor App**: https://12factor.net/config

---

## ✅ Checklist de Variables

Antes de hacer deploy:

- [ ] SPRING_PROFILES_ACTIVE=prod
- [ ] JAVA_OPTS configurado
- [ ] MAIL_USERNAME configurado
- [ ] MAIL_PASSWORD (App Password nueva)
- [ ] UPLOAD_PATH configurado
- [ ] UPLOAD_DIAGNOSTICOS_PATH configurado
- [ ] PostgreSQL añadido (DATABASE_URL automática)
- [ ] (Opcional) Cloudinary configurado
- [ ] (Opcional) AWS S3 configurado

---

**Última actualización**: Diciembre 2024

