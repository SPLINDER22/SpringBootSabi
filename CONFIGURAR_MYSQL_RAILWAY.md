# 🚂 Cómo Configurar MySQL en Railway - Guía Completa

## ❌ Problema Actual

El error `DATABASE_URL missing user credentials` significa que Railway no ha configurado correctamente las variables de entorno de MySQL.

## ✅ Solución: Configurar MySQL Paso a Paso

### Paso 1: Eliminar PostgreSQL (Si existe)

1. Ve a tu proyecto en Railway: https://railway.app
2. Encuentra el servicio **PostgreSQL** (si existe)
3. Click en él → **Settings** → **Delete Service**
4. Confirma la eliminación

### Paso 2: Agregar MySQL Database

1. En tu proyecto de Railway, click en **+ New**
2. Selecciona **Database** → **Add MySQL**
3. Railway creará un nuevo servicio de MySQL

⏳ **Espera 1-2 minutos** a que MySQL se inicialice completamente

### Paso 3: Verificar Variables de MySQL

1. Click en el servicio **MySQL** que acabas de crear
2. Ve a la pestaña **Variables**
3. Deberías ver estas variables:

```
MYSQLHOST=mysql.railway.internal
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=<password_generado>
MYSQL_URL=mysql://root:password@mysql.railway.internal:3306/railway
```

✅ **Si ves estas variables**, MySQL está configurado correctamente.

### Paso 4: Conectar MySQL con tu Aplicación

#### Opción A: Referencias Automáticas (Recomendado)

1. Click en tu servicio **Spring Boot** (sabi)
2. Ve a **Settings** → **Service Variables**
3. Click en **+ New Variable**
4. Selecciona **Add Reference**
5. En el dropdown, selecciona tu servicio **MySQL**
6. Railway te mostrará todas las variables disponibles
7. **Selecciona todas** las que empiecen con `MYSQL`:
   - ✅ `MYSQLHOST`
   - ✅ `MYSQLPORT`
   - ✅ `MYSQLDATABASE`
   - ✅ `MYSQLUSER`
   - ✅ `MYSQLPASSWORD`
8. Click **Add**

#### Opción B: Variables Manuales (Alternativa)

Si la Opción A no funciona, copia las variables manualmente:

1. Abre tu servicio **MySQL** en una pestaña
2. Copia cada variable
3. Ve a tu servicio **Spring Boot**
4. Pega cada variable con el mismo nombre

### Paso 5: Configurar Cloudinary (Requerido)

En tu servicio **Spring Boot**, agrega estas variables:

```bash
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret
```

🔗 **Obtén tus credenciales en**: https://cloudinary.com/console

### Paso 6: Hacer Push del Código Actualizado

```bash
git add .
git commit -m "Fix MySQL configuration for Railway"
git push origin main
```

Railway desplegará automáticamente.

### Paso 7: Verificar el Despliegue

1. Ve a **Deployments** en Railway
2. Click en el deployment más reciente
3. Ve a **View Logs**
4. Busca estos mensajes:

```
✅ Using individual MYSQL* variables
   Host: mysql.railway.internal
   Port: 3306
   Database: railway
   User: root
✅ MySQL DataSource configured successfully!
```

5. Si ves esos mensajes, ¡la conexión funciona!

## 🔍 Solución de Problemas

### Error: "Missing MySQL environment variables"

**Causa**: Las variables de MySQL no están vinculadas

**Solución**:
1. Ve a tu servicio Spring Boot
2. Settings → Service Variables
3. Verifica que existan: `MYSQLHOST`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`
4. Si no existen, vuelve al **Paso 4**

### Error: "The connection attempt failed"

**Causa**: El servicio MySQL no está corriendo

**Solución**:
1. Ve al servicio MySQL
2. Verifica que el estado sea **Active** (verde)
3. Si no lo es, espera 2-3 minutos o reinicia el servicio

### Error: "Access denied for user"

**Causa**: Password incorrecto

**Solución**:
1. Ve al servicio MySQL → Variables
2. Copia el valor exacto de `MYSQLPASSWORD`
3. Ve al servicio Spring Boot → Variables
4. Actualiza `MYSQLPASSWORD` con el valor correcto

### Las tablas no se crean

**Causa**: Hibernate no puede ejecutar DDL

**Solución**:
1. Verifica en los logs que la conexión se estableció
2. Las tablas se crean automáticamente con `ddl-auto=update`
3. Si no se crean, prueba cambiar temporalmente a `ddl-auto=create` en `application-prod.properties`

### Quiero ver las tablas creadas

**Opción 1: Railway CLI**

```bash
railway login
railway link
railway run mysql -h $MYSQLHOST -u $MYSQLUSER -p$MYSQLPASSWORD $MYSQLDATABASE
```

**Opción 2: MySQL Client**

1. Ve a MySQL service → Variables
2. Copia las credenciales
3. Usa cualquier cliente MySQL (MySQL Workbench, DBeaver, etc.)

## 📋 Checklist Final

Antes de desplegar, verifica:

- [ ] Servicio MySQL creado y activo en Railway
- [ ] Variables `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` configuradas
- [ ] Variables de Cloudinary configuradas
- [ ] Código actualizado pusheado a GitHub
- [ ] Deployment en Railway completado sin errores
- [ ] Logs muestran "✅ MySQL DataSource configured successfully!"
- [ ] La aplicación responde en: https://springbootsabi-production.up.railway.app

## 🎉 ¿Todo Listo?

Si completaste todos los pasos, tu aplicación debería estar funcionando en:

🌐 **https://springbootsabi-production.up.railway.app**

Intenta:
1. Abrir la URL
2. Ver el index/login
3. Iniciar sesión con el usuario admin (se crea automáticamente)
4. Crear un cliente de prueba
5. Subir una imagen (verifica que se suba a Cloudinary)

## 🆘 ¿Aún No Funciona?

Si seguiste todos los pasos y aún tienes problemas:

1. **Copia los logs completos** del deployment
2. **Copia las variables de entorno** (sin mostrar passwords)
3. Proporciona esta información para diagnóstico adicional

---

**Última actualización**: 2025-12-07

