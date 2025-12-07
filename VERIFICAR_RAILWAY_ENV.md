# ✅ Verificación de Variables de Entorno en Railway

## ⚠️ PROBLEMA DETECTADO

La aplicación no puede conectarse a la base de datos PostgreSQL porque la variable `DATABASE_URL` no está configurada correctamente o no existe.

## 📋 PASOS PARA VERIFICAR Y CORREGIR

### 1. Verificar Variables de Entorno en Railway

Ve a tu proyecto en Railway:
1. Selecciona tu servicio de **PostgreSQL**
2. Ve a la pestaña **"Variables"** o **"Connect"**
3. Deberías ver una variable llamada **`DATABASE_URL`**

### 2. Copiar la URL de la Base de Datos

La URL debería tener este formato:
```
postgresql://postgres:contraseña@host.railway.app:puerto/railway
```

O este formato:
```
postgres://postgres:contraseña@host.railway.app:puerto/railway
```

### 3. Configurar la Variable en tu Servicio de Aplicación

1. Ve a tu servicio de **aplicación Spring Boot** (sabi)
2. Ve a la pestaña **"Variables"**
3. Agrega o verifica estas variables:

```bash
DATABASE_URL=postgresql://usuario:contraseña@host:puerto/database
```

O las variables individuales:
```bash
PGHOST=host.railway.app
PGPORT=5432
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=tu_contraseña
```

### 4. Conectar la Base de Datos con la Aplicación

En Railway, puedes **referenciar** las variables del servicio PostgreSQL:

1. En tu servicio de aplicación, ve a **Variables**
2. Haz clic en **"+ New Variable"**
3. Haz clic en **"Add Reference"**
4. Selecciona el servicio **PostgreSQL**
5. Selecciona la variable **`DATABASE_URL`**
6. Railway automáticamente la compartirá entre servicios

### 5. Verificar las Variables Actuales

Para ver qué variables están disponibles, puedes agregar temporalmente esto en el `SabiApplication.java`:

```java
@PostConstruct
public void checkEnvironment() {
    System.out.println("=== ENVIRONMENT VARIABLES ===");
    System.out.println("DATABASE_URL: " + (System.getenv("DATABASE_URL") != null ? "SET" : "NOT SET"));
    System.out.println("PGHOST: " + System.getenv("PGHOST"));
    System.out.println("PGPORT: " + System.getenv("PGPORT"));
    System.out.println("PGDATABASE: " + System.getenv("PGDATABASE"));
    System.out.println("PGUSER: " + System.getenv("PGUSER"));
    System.out.println("PGPASSWORD: " + (System.getenv("PGPASSWORD") != null ? "SET" : "NOT SET"));
    System.out.println("=============================");
}
```

## 🔧 SOLUCIÓN RÁPIDA

### Opción 1: Usar DATABASE_URL (Recomendado)

En Railway Dashboard → Tu Servicio (sabi) → Variables:

```
DATABASE_URL=${{Postgres.DATABASE_URL}}
```

Esto referenciará automáticamente la URL del servicio PostgreSQL.

### Opción 2: Variables Individuales

Si `DATABASE_URL` no funciona, usa variables individuales:

```
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
```

## 🚀 DESPUÉS DE CONFIGURAR

1. **Guarda** los cambios en Railway
2. Railway **redesplegará** automáticamente la aplicación
3. Verifica los **logs** para confirmar que la conexión funciona
4. Accede a la **URL pública** de tu aplicación

## 📝 NOTAS IMPORTANTES

- Railway genera automáticamente las credenciales de PostgreSQL
- Las variables deben estar en el **servicio de la aplicación**, no solo en PostgreSQL
- Puedes usar referencias de variables con la sintaxis: `${{NombreServicio.VARIABLE}}`
- Los cambios en variables requieren un redespliegue

## ❓ SI EL PROBLEMA PERSISTE

1. Verifica que el servicio PostgreSQL esté **corriendo**
2. Comprueba que ambos servicios estén en la **misma red privada**
3. Revisa los **logs del servicio PostgreSQL**
4. Intenta **recrear** la conexión entre servicios

---

**¿Necesitas ayuda adicional?** Comparte los logs de Railway después de seguir estos pasos.

