# 🚂 Paso a Paso: Desplegar SABI en Railway

## ✅ Pre-requisitos Completados
- ✓ Archivos de configuración creados (nixpacks.toml, railway.toml)
- ✓ .gitignore configurado
- ✓ Documentación lista

---

## 📋 PASO 1: Preparar el Repositorio Git

### 1.1 Verificar el estado de Git
```powershell
cd C:\Users\USER\Downloads\SpringBootSabi
git status
```

### 1.2 Si NO es un repositorio Git, inicialízalo
```powershell
git init
git add .
git commit -m "Initial commit - Preparar para Railway"
```

### 1.3 Conectar con GitHub (si aún no lo has hecho)

#### Opción A: Repositorio nuevo
```powershell
# Crea un nuevo repositorio en GitHub (https://github.com/new)
# Nombre sugerido: SpringBootSabi
# NO inicialices con README

# Luego ejecuta:
git remote add origin https://github.com/TU-USUARIO/SpringBootSabi.git
git branch -M main
git push -u origin main
```

#### Opción B: Repositorio existente
```powershell
git remote -v  # Verificar remote actual
git add .
git commit -m "Configurar para Railway"
git push
```

---

## 📋 PASO 2: Crear Proyecto en Railway

### 2.1 Acceder a Railway
1. Ve a https://railway.app
2. Haz clic en "Login" o "Sign Up"
3. Conecta con GitHub

### 2.2 Crear Nuevo Proyecto
1. Haz clic en "New Project"
2. Selecciona "Deploy from GitHub repo"
3. Autoriza a Railway a acceder a tus repositorios (si es la primera vez)
4. Busca y selecciona tu repositorio `SpringBootSabi`

### 2.3 Railway Detectará el Proyecto
- Railway buscará archivos de configuración
- Detectará que es un proyecto Java/Maven
- Usará el archivo `nixpacks.toml` que creamos

---

## 📋 PASO 3: Agregar PostgreSQL

### 3.1 Agregar Base de Datos
1. En tu proyecto de Railway, haz clic en "+ New"
2. Selecciona "Database"
3. Elige "Add PostgreSQL"
4. Railway creará la base de datos automáticamente

### 3.2 Conectar Servicios
1. Haz clic en tu servicio de aplicación (sabi)
2. Ve a la pestaña "Settings"
3. En "Service", verifica que esté conectado con PostgreSQL
4. Railway automáticamente configurará `DATABASE_URL`

---

## 📋 PASO 4: Configurar Variables de Entorno

### 4.1 Acceder a Variables
1. Haz clic en tu servicio de aplicación
2. Ve a la pestaña "Variables"

### 4.2 Agregar Variables Obligatorias

Agrega las siguientes variables (una por una):

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activa el perfil de producción |
| `MAIL_USERNAME` | `Sabi.geas5@gmail.com` | Email de Gmail |
| `MAIL_PASSWORD` | `Williamespinel1` | Contraseña de Gmail |
| `UPLOAD_PATH` | `/app/uploads/perfiles` | Ruta para archivos de perfil |
| `UPLOAD_DIAGNOSTICOS_PATH` | `/app/uploads/diagnosticos` | Ruta para diagnósticos |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | Opciones de Java |

**IMPORTANTE**: `DATABASE_URL` ya estará configurada automáticamente por Railway.

### 4.3 Obtener Contraseña de Aplicación de Gmail

1. Ve a https://myaccount.google.com/
2. Ve a "Seguridad"
3. Activa "Verificación en dos pasos" (si no está activa)
4. Busca "Contraseñas de aplicaciones"
5. Crea una nueva:
   - Selecciona "Correo"
   - Selecciona "Otro (nombre personalizado)"
   - Escribe "SABI Railway"
6. Copia la contraseña de 16 caracteres
7. Úsala como valor de `MAIL_PASSWORD`

---

## 📋 PASO 5: Configurar Dominio

### 5.1 Generar Dominio Público
1. En tu servicio, ve a "Settings"
2. En la sección "Networking"
3. Haz clic en "Generate Domain"
4. Railway te dará un dominio como: `springbootsabi-production.up.railway.app`

### 5.2 Copiar URL del Dominio
- Guarda esta URL, la necesitarás para probar la aplicación

---

## 📋 PASO 6: Desplegar

### 6.1 Primera Compilación
1. Railway comenzará a compilar automáticamente
2. Ve a la pestaña "Deployments"
3. Haz clic en el deployment activo
4. Ve a "View Logs"

### 6.2 Verificar Logs
Busca estos mensajes en los logs:

✓ **Build exitoso:**
```
[INFO] BUILD SUCCESS
[INFO] Building jar: /app/sabi/target/sabi-0.0.1-SNAPSHOT.jar
```

✓ **Aplicación iniciada:**
```
Started SabiApplication in X.XXX seconds
```

✓ **Conexión a base de datos:**
```
HikariPool-1 - Start completed
```

### 6.3 Errores Comunes y Soluciones

#### Error: "Could not find or load main class"
**Solución:** Verifica que el archivo JAR se generó correctamente
```
Archivo esperado: sabi/target/sabi-0.0.1-SNAPSHOT.jar
```

#### Error: "Connection refused to database"
**Solución:** 
- Verifica que PostgreSQL esté corriendo
- Verifica que DATABASE_URL esté configurada
- Verifica que ambos servicios estén en el mismo proyecto

#### Error: "Port already in use"
**Solución:** Railway asigna el puerto automáticamente a través de `$PORT`

#### Error: "OutOfMemoryError"
**Solución:** Aumenta JAVA_OPTS:
```
JAVA_OPTS=-Xmx1024m -Xms512m
```

---

## 📋 PASO 7: Verificar Deployment

### 7.1 Acceder a la Aplicación
1. Ve a tu dominio: `https://tu-app.up.railway.app`
2. Deberías ver la página de inicio de SABI

### 7.2 Probar Funcionalidades

#### Probar Registro:
1. Ve a `/auth/registro`
2. Crea una nueva cuenta
3. Verifica que el registro se complete

#### Probar Login:
1. Ve a `/auth/login`
2. Inicia sesión con la cuenta creada
3. Verifica que accedes al dashboard

#### Probar Correos:
1. Usa una función que envíe correos
2. Verifica que el correo llegue
3. Si no llega, revisa los logs para errores de SMTP

### 7.3 Verificar Base de Datos

#### Opción 1: Desde Railway
1. Haz clic en el servicio PostgreSQL
2. Ve a "Data"
3. Verifica que las tablas se hayan creado

#### Opción 2: Cliente SQL
1. En PostgreSQL, ve a "Connect"
2. Copia las credenciales
3. Conecta con un cliente como DBeaver o pgAdmin
4. Verifica las tablas: `usuario`, `cliente`, `entrenador`, etc.

---

## 📋 PASO 8: Configuración Post-Deployment

### 8.1 Crear Usuario Admin

**Opción A: Usando SQL directamente**
1. Conecta a la base de datos PostgreSQL
2. Ejecuta:
```sql
-- Primero, registra un usuario normalmente desde la aplicación
-- Luego actualiza su rol:
UPDATE usuario SET role = 'ADMIN' WHERE email = 'tu-email@ejemplo.com';
```

**Opción B: Desde la aplicación**
1. Registra un usuario normalmente
2. Ve a Railway → PostgreSQL → Data
3. Encuentra el usuario en la tabla `usuario`
4. Cambia `role` de `CLIENTE` a `ADMIN`

### 8.2 Verificar Funcionalidades de Admin
1. Inicia sesión con el usuario admin
2. Ve a `/admin`
3. Verifica que puedas:
   - Ver lista de usuarios
   - Gestionar entrenadores
   - Verificar entrenadores
   - Ver estadísticas

---

## 📋 PASO 9: Monitoreo y Mantenimiento

### 9.1 Monitorear Aplicación
Railway proporciona:
- **Logs en tiempo real**: Ver actividad de la aplicación
- **Métricas**: CPU, memoria, red
- **Alertas**: Configurar notificaciones

### 9.2 Ver Logs
1. Ve a tu servicio
2. Haz clic en "Deployments"
3. Selecciona el deployment activo
4. Haz clic en "View Logs"

### 9.3 Reiniciar Servicio
Si necesitas reiniciar:
1. Ve a "Settings"
2. Haz clic en "Restart"

---

## 📋 PASO 10: Actualizaciones

### 10.1 Actualizar Código
```powershell
# 1. Haz cambios en tu código local
# 2. Commit y push
git add .
git commit -m "Descripción de los cambios"
git push

# 3. Railway detectará los cambios y desplegará automáticamente
```

### 10.2 Actualizar Variables de Entorno
1. Ve a "Variables"
2. Modifica o agrega variables
3. Railway reiniciará automáticamente

---

## ⚠️ ADVERTENCIAS IMPORTANTES

### Almacenamiento de Archivos
Railway usa **almacenamiento efímero**. Los archivos subidos (fotos de perfil, diagnósticos) se perderán cuando:
- El contenedor se reinicie
- Haya un nuevo deployment
- El servicio se detenga

**Soluciones:**
1. **Cloudinary** (Recomendado - Gratis hasta 25GB)
2. **AWS S3** (Escalable pero de pago)
3. **Railway Volumes** (Cuando esté disponible)

### Base de Datos
- **IMPORTANTE**: La base de datos PostgreSQL de Railway es persistente
- Haz backups regulares
- Railway ofrece backups automáticos en planes pagos

### Costos
- **Plan Starter**: $5 USD de crédito mensual (gratis)
- Aproximadamente 500 horas de ejecución
- Si excedes, la aplicación se pausará hasta el siguiente mes
- O puedes agregar un método de pago

---

## 📞 Soporte

### Si algo sale mal:

1. **Revisa los logs**: La mayoría de errores están en los logs
2. **Verifica variables**: Asegúrate de que todas estén configuradas
3. **Consulta la documentación**: https://docs.railway.app
4. **Comunidad**: https://discord.gg/railway

---

## ✅ Checklist Final

Antes de considerar el deployment completo:

- [ ] Aplicación accesible desde el dominio público
- [ ] Puedes registrar nuevos usuarios
- [ ] Puedes iniciar sesión
- [ ] Los correos se envían correctamente
- [ ] Las tablas de la base de datos se crearon
- [ ] Puedes acceder al panel de admin
- [ ] Los entrenadores pueden registrarse
- [ ] Los clientes pueden ver entrenadores
- [ ] Las rutas públicas funcionan sin login
- [ ] Las rutas protegidas requieren login
- [ ] Los logs no muestran errores críticos

---

## 🎉 ¡Felicidades!

Si completaste todos los pasos, tu aplicación SABI está corriendo en Railway.

**Próximos pasos recomendados:**
1. Configurar Cloudinary para almacenamiento de archivos
2. Configurar un dominio personalizado
3. Implementar SSL (Railway lo hace automáticamente)
4. Configurar backups de base de datos
5. Implementar monitoreo avanzado

---

**Autor**: GitHub Copilot  
**Fecha**: Diciembre 2025  
**Versión**: 1.0

