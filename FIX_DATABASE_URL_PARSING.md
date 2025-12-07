# 🔧 FIX: DATABASE_URL Parsing Error en Railway

## ❌ PROBLEMA DETECTADO

### Error en los logs:
```
WARN org.postgresql.util.PGPropertyUtil: JDBC URL invalid port number: port
RuntimeException: Driver org.postgresql.Driver claims to not accept jdbcUrl, 
jdbc:postgresql://user:password@host:port/database
```

### Causa:
El código **NO estaba parseando correctamente** la variable `DATABASE_URL` de Railway.

Railway proporciona:
```
DATABASE_URL=postgresql://username:password@hostname:5432/dbname
```

Pero la aplicación estaba usando literalmente:
```
jdbc:postgresql://user:password@host:port/database
```

## ✅ SOLUCIÓN APLICADA

### 1. Actualizado `DataSourceConfig.java`

**Antes:**
- Simplemente agregaba `jdbc:` al inicio
- NO parseaba los valores reales (user, pass, host, port, db)

**Ahora:**
```java
@Bean
@Primary
public DataSource dataSource() {
    String databaseUrl = System.getenv("DATABASE_URL");
    URI dbUri = new URI(databaseUrl);
    
    // Parsea CORRECTAMENTE cada componente
    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String host = dbUri.getHost();
    int port = dbUri.getPort();
    String database = dbUri.getPath().substring(1);
    
    // Construye la URL JDBC correcta
    String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", 
                                    host, port, database);
    
    // Configura HikariCP con los valores correctos
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(jdbcUrl);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    dataSource.setDriverClassName("org.postgresql.Driver");
    
    return dataSource;
}
```

### 2. Actualizado `application-prod.properties`

Agregado:
```properties
spring.datasource.url=${JDBC_DATABASE_URL:${DATABASE_URL}}
```

Esto permite que Spring Boot lea la URL, pero `DataSourceConfig` la procesa correctamente.

## 🚀 CÓMO DESPLEGAR EL FIX

### Opción 1: Script Automático (Recomendado)
```powershell
cd C:\Users\USER\Downloads\SpringBootSabi
.\deploy-railway.ps1
```

### Opción 2: Manual
```powershell
cd C:\Users\USER\Downloads\SpringBootSabi\sabi

# 1. Verificar cambios
git status

# 2. Agregar cambios
git add src/main/java/com/sabi/sabi/config/DataSourceConfig.java
git add src/main/resources/application-prod.properties

# 3. Commit
git commit -m "Fix DATABASE_URL parsing for Railway PostgreSQL"

# 4. Push a Railway
git push origin main
```

## ✅ VERIFICACIÓN POST-DEPLOY

Una vez deployado, verifica:

### 1. En los logs de Railway deberías ver:
```
✅ DATABASE_URL converted successfully
   Host: postgres.railway.internal
   Port: 5432
   Database: railway
   User: postgres
```

### 2. La app debe iniciar sin errores:
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

### 3. El health check debe funcionar:
```bash
curl https://tu-app.railway.app/health
```
Respuesta esperada:
```json
{
  "status": "UP",
  "application": "Sabi",
  "timestamp": "..."
}
```

## 📊 CAMBIOS REALIZADOS

### Archivos modificados:
1. ✅ `sabi/src/main/java/com/sabi/sabi/config/DataSourceConfig.java`
   - Parser mejorado de `DATABASE_URL`
   - Configuración explícita de HikariCP
   - Logging de debug para verificación

2. ✅ `sabi/src/main/resources/application-prod.properties`
   - Agregada configuración de `spring.datasource.url`

### Archivos nuevos:
3. ✅ `deploy-railway.ps1`
   - Script de deploy automatizado

## ⚙️ CONFIGURACIÓN HIKARICP

El fix incluye configuración optimizada para Railway:

```java
dataSource.setMaximumPoolSize(5);      // Máx 5 conexiones (Railway limita)
dataSource.setMinimumIdle(2);          // Mín 2 conexiones activas
dataSource.setConnectionTimeout(30000); // 30 segundos timeout
dataSource.setIdleTimeout(600000);     // 10 minutos idle
dataSource.setMaxLifetime(1800000);    // 30 minutos max lifetime
```

Estos valores están optimizados para:
- Evitar límite de conexiones de Railway
- Minimizar uso de recursos
- Mejorar estabilidad

## 🔍 TROUBLESHOOTING

### Si el error persiste:

#### 1. Verificar que `DATABASE_URL` existe en Railway:
```bash
railway variables
```

Debe mostrar:
```
DATABASE_URL=postgresql://postgres:***@postgres.railway.internal:5432/railway
```

#### 2. Verificar logs de Railway:
```bash
railway logs
```

Buscar línea:
```
✅ DATABASE_URL converted successfully
```

#### 3. Si no aparece esa línea:
- El perfil `prod` NO está activo
- Verifica variable `SPRING_PROFILES_ACTIVE=prod` en Railway

#### 4. Si aparece pero sigue error:
- La `DATABASE_URL` de Railway está en formato incorrecto
- Contacta soporte de Railway

## 📝 NOTAS IMPORTANTES

### ⚠️ Encoding UTF-8
El fix anterior del encoding **YA ESTÁ SOLUCIONADO**. Los archivos `.properties` ahora usan ASCII para las URLs.

### ⚠️ PostgreSQL Driver
La dependencia de PostgreSQL **YA ESTÁ** en `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### ⚠️ Profile Activation
El perfil `prod` se activa automáticamente con:
```
SPRING_PROFILES_ACTIVE=prod
```

Esta variable **DEBE** estar configurada en Railway Settings → Variables.

## 🎯 RESULTADO ESPERADO

Después del deploy, la aplicación debe:

1. ✅ Conectarse correctamente a PostgreSQL de Railway
2. ✅ Crear las tablas automáticamente (`ddl-auto=update`)
3. ✅ Responder en el puerto asignado por Railway
4. ✅ Health checks funcionando

### Logs exitosos:
```
2025-12-07T19:30:00.000Z  INFO - Starting SabiApplication
2025-12-07T19:30:05.000Z  INFO - ✅ DATABASE_URL converted successfully
2025-12-07T19:30:06.000Z  INFO - HikariPool-1 - Starting...
2025-12-07T19:30:07.000Z  INFO - HikariPool-1 - Start completed.
2025-12-07T19:30:10.000Z  INFO - Started SabiApplication in 10.5 seconds
2025-12-07T19:30:10.000Z  INFO - Tomcat started on port 8080
```

## 📞 SOPORTE

Si después de aplicar este fix el error persiste:

1. Revisa los logs completos de Railway
2. Verifica las variables de entorno
3. Confirma que el perfil `prod` está activo
4. Verifica que PostgreSQL está añadido al proyecto en Railway

---

**Fecha del Fix**: 2025-12-07  
**Versión**: 2.0  
**Status**: ✅ LISTO PARA DEPLOY

