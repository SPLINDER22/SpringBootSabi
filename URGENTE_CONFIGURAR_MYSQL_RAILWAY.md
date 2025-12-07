# 🚨 ACCIÓN INMEDIATA: Configurar MySQL en Railway

## 📋 **LO QUE DEBES HACER AHORA MISMO EN RAILWAY**

### 🎯 **PASO 1: Ir a tu Servicio MySQL en Railway**

1. Abre tu proyecto en Railway: https://railway.app/dashboard
2. Click en tu servicio **MySQL** (no en tu aplicación)
3. Ve a la pestaña **Variables** o **Connect**

### 📝 **PASO 2: Copiar Estas Variables del MySQL**

Busca y COPIA estos valores exactos que Railway te muestra:

```
MYSQLHOST       → Ejemplo: containers-us-west-123.railway.app
MYSQLPORT       → Ejemplo: 6379
MYSQLDATABASE   → Ejemplo: railway
MYSQLUSER       → Ejemplo: root
MYSQLPASSWORD   → Ejemplo: abc123xyz456def789
MYSQLURL        → Ejemplo: mysql://root:abc123xyz456@containers-us-west-123.railway.app:6379/railway
```

### 🎯 **PASO 3: Ir a tu Servicio de Aplicación**

1. Click en tu servicio **springbootsabi** (tu aplicación)
2. Ve a **Variables**

### ➕ **PASO 4: Agregar TODAS estas Variables**

Copia y pega cada variable con su valor correspondiente:

```plaintext
MYSQLHOST=<el valor que copiaste>
MYSQLPORT=<el valor que copiaste>
MYSQLDATABASE=<el valor que copiaste>
MYSQLUSER=<el valor que copiaste>
MYSQLPASSWORD=<el valor que copiaste>
MYSQLURL=<el valor que copiaste>
SPRING_PROFILES_ACTIVE=prod
```

**🚨 IMPORTANTE:** 
- Reemplaza `<el valor que copiaste>` con los valores reales de tu MySQL
- NO pongas comillas ni espacios
- Copia EXACTAMENTE como aparece en Railway

### ✅ **PASO 5: Variables Adicionales (opcionales)**

Si quieres email y otras funciones, agrega también:

```plaintext
MAIL_USERNAME=Sabi.geas5@gmail.com
MAIL_PASSWORD=Williamespinel1
UPLOAD_PATH=/app/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/app/uploads/diagnosticos
TZ=America/Bogota
```

### 🔄 **PASO 6: Railway se Redeploy Automáticamente**

Después de agregar las variables, Railway debería hacer redeploy automáticamente.

### 🎯 **PASO 7: Verificar los Logs**

Ve a **Deployments** → Click en el último deployment → **Logs**

Deberías ver:

```
✅ Using individual MYSQL* variables
   Host: containers-us-west-XXX.railway.app
   Port: 6379
   Database: railway
   User: root
✅ MySQL DataSource configured successfully!
```

## 🆘 **SI NO VES LAS VARIABLES EN EL SERVICIO MySQL**

Esto significa que el servicio MySQL no está correctamente creado. Hazlo así:

### 1. Agregar MySQL desde Cero

1. En tu proyecto Railway
2. Click en **+ New** → **Database** → **Add MySQL**
3. Espera a que se provisione (1-2 minutos)
4. Una vez listo, ve a ese servicio MySQL y copia las variables

### 2. Conectar con tu Aplicación

En tu servicio de aplicación (springbootsabi):

1. Variables → **Add Reference**
2. Selecciona el servicio **MySQL**
3. Marca todas las variables que comienzan con `MYSQL*`
4. Save

## 📸 **EJEMPLO VISUAL DE DÓNDE ESTÁN LAS VARIABLES**

```
Railway Dashboard
├── Tu Proyecto
    ├── springbootsabi (tu app)
    │   └── Variables ← AQUÍ agregas las variables MYSQL*
    └── MySQL (base de datos)
        └── Variables ← AQUÍ copias los valores
```

## ⚡ **COMANDO DE EMERGENCIA (SI TODO FALLA)**

Si después de todo esto no funciona, ve a tu `DataSourceConfig.java` y agrega temporalmente debugging:

```java
System.out.println("=== ALL ENVIRONMENT VARIABLES ===");
System.getenv().forEach((key, value) -> {
    if (key.toUpperCase().contains("MYSQL") || key.toUpperCase().contains("DATABASE")) {
        System.out.println(key + " = " + (key.contains("PASSWORD") ? "****" : value));
    }
});
```

Esto te mostrará EXACTAMENTE qué variables tiene Railway.

## ✅ **VERIFICACIÓN FINAL**

Deberías tener estas variables en **springbootsabi**:

- [x] MYSQLHOST
- [x] MYSQLPORT
- [x] MYSQLDATABASE
- [x] MYSQLUSER
- [x] MYSQLPASSWORD
- [x] SPRING_PROFILES_ACTIVE=prod

---

**🕐 Tiempo estimado**: 5 minutos
**📅 Fecha**: 2025-12-07 20:51


