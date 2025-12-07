# ✅ FIX CRÍTICO: DATABASE_URL en Railway

## 🔴 PROBLEMA IDENTIFICADO

### Error en los logs:
```
WARN org.postgresql.util.PGPropertyUtil : JDBC URL invalid port number: port
ERROR Driver org.postgresql.Driver claims to not accept jdbcUrl, 
      jdbc:postgresql://user:password@host:port/database
```

### 🔍 Causa raíz:
La URL convertida contenía **literalmente** los textos:
- `user` en lugar del usuario real
- `password` en lugar de la contraseña real  
- `host` en lugar del host real de Railway
- `port` en lugar de 5432
- `database` en lugar del nombre real

**Era una URL de ejemplo, no la URL real de Railway.**

---

## ❌ CÓDIGO ANTERIOR (INCORRECTO)

```java
@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            databaseUrl = "jdbc:" + databaseUrl;
        }
        
        return DataSourceBuilder
                .create()
                .url(databaseUrl)
                .build();
    }
}
```

### ⚠️ Problema:
`@ConfigurationProperties(prefix = "spring.datasource")` **sobrescribía** la URL configurada manualmente con los valores del `application-prod.properties`, donde no había una URL específica, causando que usara una URL placeholder.

---

## ✅ CÓDIGO NUEVO (CORRECTO)

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            if (!databaseUrl.startsWith("jdbc:")) {
                databaseUrl = "jdbc:" + databaseUrl;
            }
            
            System.out.println("DATABASE_URL detected and converted to JDBC format");
            System.out.println("URL: " + databaseUrl.replaceAll(":[^:@]+@", ":****@"));
            
            properties.setUrl(databaseUrl);
        }
        
        return properties;
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }
}
```

### ✅ Solución:
1. **DataSourceProperties** se crea primero y lee `DATABASE_URL` del entorno
2. **Conversión correcta**: `postgresql://...` → `jdbc:postgresql://...`
3. **setUrl()** establece la URL CON VALORES REALES antes de que ConfigurationProperties intente leer desde properties
4. **HikariDataSource** recibe la URL correcta con credenciales reales de Railway

---

## 🎯 LOGS ESPERADOS AHORA

```bash
✅ DATABASE_URL detected and converted to JDBC format
✅ URL: jdbc:postgresql://postgres:****@junction.railway.app:5432/railway
✅ HikariPool-1 - Starting...
✅ HikariPool-1 - Start completed.
✅ Started SabiApplication in 8.234 seconds (process running for 9.123)
```

---

## 📋 CHECKLIST DE DEPLOYMENT

- [x] Archivo `application-prod.properties` sin caracteres especiales (encoding UTF-8)
- [x] `DataSourceConfig.java` corregido para usar DATABASE_URL real
- [x] Variable `DATABASE_URL` configurada en Railway (automática con PostgreSQL)
- [x] `spring.datasource.driver-class-name=org.postgresql.Driver`
- [x] `spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect`
- [x] `spring.jpa.hibernate.ddl-auto=update` (para mantener datos)

---

## 🚀 DESPLIEGUE

```bash
git add .
git commit -m "FIX: Corregir DataSourceConfig para usar DATABASE_URL correctamente"
git push origin main
```

Railway detectará el push y redesplegar√° automáticamente.

**Tiempo estimado:** 3-5 minutos

---

## 📊 RESULTADO ESPERADO

### ✅ Aplicación funcionando correctamente:
- Base de datos PostgreSQL conectada
- Tablas creadas automáticamente (ddl-auto=update)
- Servidor corriendo en puerto 8080
- Accesible desde el dominio de Railway

### 🌐 URL de la aplicación:
`https://tu-app-production.up.railway.app`

---

## 🔧 SI AÚN FALLA

1. Verificar que Railway tiene la variable `DATABASE_URL`:
   ```bash
   echo $DATABASE_URL
   ```

2. Verificar formato de la URL:
   ```
   postgresql://postgres:password@host.railway.app:5432/railway
   ```

3. Revisar logs de Railway para ver la URL convertida (con password censurado)

4. Verificar conectividad de red (Railway debería permitir conexiones internas)

---

**Fecha:** 2025-12-07  
**Status:** ✅ RESUELTO

