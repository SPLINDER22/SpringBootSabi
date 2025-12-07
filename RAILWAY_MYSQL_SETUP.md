# 🚂 Railway Deployment - MySQL Configuration

## ✅ Cambios Realizados

La aplicación ahora está configurada para usar **MySQL** en lugar de PostgreSQL.

## 📋 Pasos para Desplegar en Railway con MySQL

### 1. Eliminar la Base de Datos PostgreSQL Actual

1. Ve a tu proyecto en Railway: https://railway.app
2. En el dashboard del proyecto, busca el servicio de **PostgreSQL**
3. Haz clic en él → Settings → **Delete Service**
4. Confirma la eliminación

### 2. Agregar MySQL Database

1. En tu proyecto de Railway, haz clic en **+ New**
2. Selecciona **Database** → **Add MySQL**
3. Railway creará automáticamente un servicio de MySQL con todas las variables necesarias

### 3. Verificar Variables de Entorno

El servicio de MySQL en Railway automáticamente crea estas variables:
- `MYSQLHOST` - Host de la base de datos
- `MYSQLPORT` - Puerto (normalmente 3306)
- `MYSQLDATABASE` - Nombre de la base de datos
- `MYSQLUSER` - Usuario
- `MYSQLPASSWORD` - Contraseña
- `MYSQLURL` - URL completa (mysql://user:pass@host:port/db)

**No necesitas configurar nada más** - la aplicación las detectará automáticamente.

### 4. Variables Adicionales (Opcionales)

En el servicio de **Spring Boot** (no en MySQL), puedes agregar:

```bash
# Cloudinary (Requerido para imágenes)
CLOUDINARY_CLOUD_NAME=tu_cloud_name
CLOUDINARY_API_KEY=tu_api_key
CLOUDINARY_API_SECRET=tu_api_secret

# Email (Opcional)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_app
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

### 5. Vincular MySQL con tu Aplicación

1. Haz clic en tu servicio de **Spring Boot** (sabi)
2. Ve a la pestaña **Variables**
3. Haz clic en **+ Add Reference**
4. Selecciona el servicio de **MySQL**
5. Agrega todas las variables que Railway te sugiera (MYSQLHOST, MYSQLPORT, etc.)

### 6. Hacer Commit y Push

```bash
git add .
git commit -m "Cambiar de PostgreSQL a MySQL para Railway"
git push origin main
```

Railway detectará los cambios y desplegará automáticamente.

### 7. Verificar el Despliegue

1. Ve a la pestaña **Deployments** en Railway
2. Espera a que el build termine (tarda 2-5 minutos)
3. Revisa los logs para ver que todo funcione:
   - Busca el mensaje: `✅ MySQL DataSource configured successfully!`
   - Verifica que Hibernate inicie correctamente
   - Busca: `Started SabiApplication`

4. Abre tu aplicación en: https://springbootsabi-production.up.railway.app

## 🎯 Ventajas de MySQL en Railway

✅ **Más estable** que PostgreSQL en Railway
✅ **Mejor compatibilidad** con Spring Boot
✅ **Configuración automática** más confiable
✅ **Menos problemas de conexión**
✅ **Ampliamente usado** y probado

## 🔧 Solución de Problemas

### Si no se conecta a MySQL:

1. Verifica que el servicio MySQL esté **Running** (verde)
2. Verifica que las variables estén referenciadas correctamente
3. Revisa los logs del servicio Spring Boot

### Si las tablas no se crean:

- La aplicación usa `spring.jpa.hibernate.ddl-auto=update`
- Las tablas se crean automáticamente en el primer despliegue
- Los datos persisten entre despliegues

### Si necesitas resetear la base de datos:

1. Ve al servicio MySQL en Railway
2. Settings → Data → **Reset Database**
3. Redespliega la aplicación

## 📝 Notas Importantes

- **No necesitas modificar código** - todo está configurado
- **Los datos se mantienen** entre despliegues
- **Cloudinary sigue siendo necesario** para las imágenes
- **La URL de la app no cambia**: https://springbootsabi-production.up.railway.app

## 🚀 Próximos Pasos

Después de desplegar con MySQL:

1. ✅ Verifica que la aplicación inicie correctamente
2. ✅ Prueba el login con el usuario admin
3. ✅ Verifica que las imágenes suban a Cloudinary
4. ✅ Prueba crear un cliente y asignar rutinas

---

**¿Listo para desplegar?** Sigue los pasos arriba y tu aplicación estará funcionando con MySQL en Railway! 🎉

