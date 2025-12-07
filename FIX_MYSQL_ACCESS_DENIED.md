# 🔧 Solución: Error MySQL Access Denied en Railway

## ❌ Error Actual
```
Access denied for user 'root'@'10.200.220.15' (using password: YES)
```

## 🎯 Problema
Railway MySQL no está proporcionando las variables de entorno correctas o las credenciales no coinciden.

## ✅ SOLUCIÓN PASO A PASO

### 1️⃣ Verificar Variables de Entorno en Railway

Ve al panel de Railway y verifica que existan estas variables:

```
MYSQLHOST=<hostname de mysql>
MYSQLPORT=3306
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=<password generado>
MYSQLURL=mysql://root:<password>@<host>:<port>/railway
```

### 2️⃣ Obtener las Variables Correctas de Railway MySQL

1. En tu proyecto Railway, ve a la pestaña **MySQL**
2. Ve a **Variables** o **Connect**
3. Copia las siguientes variables:
   - `MYSQLHOST`
   - `MYSQLPORT` 
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`
   - `MYSQLURL`

### 3️⃣ Configurar Variables en el Servicio de Aplicación

1. Ve a tu servicio de aplicación (springbootsabi)
2. Ve a **Variables**
3. Agrega manualmente las variables que copiaste del servicio MySQL:

```
MYSQLHOST=containers-us-west-XXX.railway.app
MYSQLPORT=6379
MYSQLDATABASE=railway
MYSQLUSER=root
MYSQLPASSWORD=tu_password_aqui
```

**⚠️ IMPORTANTE**: No uses `MYSQL_ROOT_PASSWORD`, usa `MYSQLPASSWORD`

### 4️⃣ Verificar Configuración de Referencias

Si Railway tiene la opción de "Reference Variables":

1. En tu servicio de aplicación
2. Variables → Add Reference
3. Selecciona el servicio MySQL
4. Selecciona todas las variables `MYSQL*`

### 5️⃣ Comandos para Debugging

Agrega esto temporalmente al `DataSourceConfig.java` para ver qué variables están disponibles:

```java
System.out.println("=== ALL MYSQL ENV VARS ===");
System.getenv().forEach((key, value) -> {
    if (key.toUpperCase().contains("MYSQL")) {
        System.out.println(key + "=" + (key.contains("PASSWORD") ? "****" : value));
    }
});
```

## 🔄 Re-deploy

Después de configurar las variables:

```bash
cd C:\Users\USER\Downloads\SpringBootSabi
git add .
git commit -m "Fix: Configure MySQL environment variables"
git push origin main
```

## 🧪 Verificar en los Logs

Después del deploy, busca en los logs:

```
=== 🔍 RAILWAY MySQL DATABASE CONFIGURATION ===
✅ Using individual MYSQL* variables
   Host: containers-us-west-XXX.railway.app
   Port: 6379
   Database: railway
   User: root
```

## 📋 Checklist de Variables

- [ ] `MYSQLHOST` existe y es correcto
- [ ] `MYSQLPORT` existe (generalmente 6379 o 3306)
- [ ] `MYSQLDATABASE` existe (generalmente "railway")
- [ ] `MYSQLUSER` existe (generalmente "root")
- [ ] `MYSQLPASSWORD` existe y es correcto
- [ ] Las variables están en el servicio de **aplicación**, no solo en MySQL

## 🆘 Si Aún No Funciona

### Opción A: Usar Connection String Directa

Agrega esta variable en Railway:

```
MYSQLURL=mysql://root:PASSWORD@HOST:PORT/railway
```

Reemplaza:
- `PASSWORD`: con el password real de MySQL
- `HOST`: con el host de MySQL (ej: `containers-us-west-XXX.railway.app`)
- `PORT`: con el puerto (ej: `6379`)

### Opción B: Hardcodear Temporalmente (Solo para Testing)

En `DataSourceConfig.java`, temporalmente hardcodea los valores:

```java
String mysqlHost = System.getenv("MYSQLHOST");
if (mysqlHost == null) {
    mysqlHost = "TU_HOST_AQUI.railway.app";
}
```

**⚠️ NO SUBAS ESTE CÓDIGO A GIT**

## 🔍 Cómo Obtener los Valores Correctos

1. En Railway Dashboard
2. Selecciona tu proyecto
3. Click en el servicio **MySQL**
4. Pestaña **Variables** o **Connect**
5. Copia cada valor mostrado

## 📝 Notas Importantes

- Railway MySQL usa **puerto personalizado** (no siempre 3306)
- El host es algo como `containers-us-west-XXX.railway.app`
- El password es generado automáticamente por Railway
- Las variables deben estar en el servicio de **aplicación**, no solo en MySQL

## ✅ Resultado Esperado

Cuando funcione correctamente, verás en los logs:

```
✅ Using individual MYSQL* variables
   Host: containers-us-west-XXX.railway.app
   Port: 6379
   Database: railway
   User: root
✅ MySQL DataSource configured successfully!
Hibernate: create table if not exists usuarios (...)
```

---

**Última actualización**: 2025-12-07

