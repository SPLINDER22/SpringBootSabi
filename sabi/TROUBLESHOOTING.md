# 🚨 TROUBLESHOOTING - Problemas Comunes en Railway

## 📋 Índice
1. [Problemas de Base de Datos](#1-problemas-de-base-de-datos)
2. [Problemas de Memoria](#2-problemas-de-memoria)
3. [Problemas con Archivos](#3-problemas-con-archivos)
4. [Problemas de Compilación](#4-problemas-de-compilación)
5. [Problemas de Red/Puerto](#5-problemas-de-redpuerto)
6. [Problemas con Migraciones](#6-problemas-con-migraciones)

---

## 1. Problemas de Base de Datos

### ❌ Error: "Unknown database 'sabi'"
**Causa**: Intentando conectar a MySQL en vez de PostgreSQL

**Solución**:
```bash
# Verificar que SPRING_PROFILES_ACTIVE=prod
# Verificar que DATABASE_URL existe en Railway
```

### ❌ Error: "Connection refused"
**Causa**: PostgreSQL no está añadido o DATABASE_URL incorrecta

**Solución**:
1. En Railway: New → Database → Add PostgreSQL
2. Verifica que la variable `DATABASE_URL` existe automáticamente
3. Redespliega la aplicación

### ❌ Error: "Relation 'usuario' does not exist"
**Causa**: Tablas no se crearon automáticamente

**Solución**:
```properties
# En application-prod.properties, verifica:
spring.jpa.hibernate.ddl-auto=update
```

Si persiste:
```properties
# Cambiar temporalmente a:
spring.jpa.hibernate.ddl-auto=create

# Luego volver a:
spring.jpa.hibernate.ddl-auto=update
```

---

## 2. Problemas de Memoria

### ❌ Error: "OutOfMemoryError: Java heap space"
**Causa**: Memoria insuficiente

**Solución 1 - Aumentar memoria**:
```bash
# En Railway Variables:
JAVA_OPTS=-Xmx768m -Xms384m
```

**Solución 2 - Optimizar queries**:
```java
// Evitar cargar todas las entidades de una vez
// Usar paginación:
Pageable pageable = PageRequest.of(0, 20);
```

**Solución 3 - Actualizar plan Railway**:
- Plan gratuito: 512 MB RAM
- Plan Developer: hasta 8 GB RAM

### ❌ App se reinicia constantemente
**Causa**: OOM Killer matando el proceso

**Solución**:
```bash
# Reducir consumo:
JAVA_OPTS=-Xmx400m -Xms200m

# O actualizar a plan con más RAM
```

---

## 3. Problemas con Archivos

### ❌ Archivos desaparecen después de reiniciar
**Causa**: Railway usa almacenamiento efímero en `/tmp`

**Solución Temporal**:
- Aceptar que los archivos son temporales
- Documentar a usuarios que deben re-subir después de mantenimiento

**Solución Permanente - Cloudinary (Recomendado)**:

1. Crear cuenta en https://cloudinary.com

2. Agregar dependencia:
```xml
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.36.0</version>
</dependency>
```

3. Configurar en Railway:
```bash
CLOUDINARY_URL=cloudinary://api_key:api_secret@cloud_name
```

4. Crear servicio:
```java
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    
    public CloudinaryService(@Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }
    
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }
}
```

**Solución Permanente - AWS S3**:

1. Crear bucket en AWS

2. Agregar dependencia:
```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.0</version>
</dependency>
```

3. Variables en Railway:
```bash
AWS_ACCESS_KEY_ID=tu_key
AWS_SECRET_ACCESS_KEY=tu_secret
AWS_REGION=us-east-1
S3_BUCKET_NAME=sabi-uploads
```

### ❌ Error al subir archivos grandes
**Causa**: Límites de tamaño

**Solución**:
```properties
# application.properties
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=25MB
server.tomcat.max-swallow-size=30MB
```

---

## 4. Problemas de Compilación

### ❌ Build falla: "Failed to execute goal"
**Causa**: Error en compilación Maven

**Diagnóstico**:
```bash
# Compilar localmente:
mvn clean package -DskipTests

# Ver detalles del error
```

**Soluciones comunes**:

1. **Error de Java Version**:
```xml
<!-- pom.xml -->
<properties>
    <java.version>21</java.version>
</properties>
```

2. **Error de encoding**:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

3. **Lombok no funciona**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

### ❌ Tests fallan en Railway
**Causa**: Tests requieren recursos no disponibles

**Solución**:
```bash
# En nixpacks.toml o railway.json:
"buildCommand": "mvn clean package -DskipTests"
```

---

## 5. Problemas de Red/Puerto

### ❌ Error: "Application failed to respond"
**Causa**: App no escucha en el puerto correcto

**Solución**:
```properties
# application-prod.properties
server.port=${PORT:8080}
```

**Verificar en logs**:
```
Tomcat started on port(s): 8080 (http)
```

### ❌ Error: "502 Bad Gateway"
**Causa**: App tarda mucho en iniciar o crashea

**Solución 1 - Aumentar timeout**:
Railway automáticamente espera hasta 5 minutos

**Solución 2 - Reducir tiempo de inicio**:
```properties
# Desactivar features innecesarios
spring.jpa.show-sql=false
logging.level.root=WARN
```

**Solución 3 - Health check**:
```java
@GetMapping("/health")
public ResponseEntity<String> health() {
    return ResponseEntity.ok("OK");
}
```

### ❌ CORS errors en frontend
**Causa**: Frontend en dominio diferente

**Solución**:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://tu-dominio.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

---

## 6. Problemas con Migraciones

### ❌ Flyway fails: "Validate failed"
**Causa**: Migración inconsistente

**Solución 1 - Reparar**:
```sql
-- Conectar a PostgreSQL en Railway y ejecutar:
DELETE FROM flyway_schema_history WHERE success = false;
```

**Solución 2 - Limpiar**:
```properties
# CUIDADO: Borra toda la base de datos
spring.jpa.hibernate.ddl-auto=create-drop
```

### ❌ Datos de prueba no se cargan
**Causa**: DataInitializer no se ejecuta en prod

**Solución**:
```java
@Component
@Profile({"h2", "prod"}) // Añadir prod
public class DataInitializer implements CommandLineRunner {
    // ...
}
```

---

## 🔧 Comandos Útiles Railway CLI

```bash
# Instalar CLI
npm i -g @railway/cli

# Login
railway login

# Ver logs en tiempo real
railway logs

# Ver variables
railway variables

# Conectar a base de datos
railway connect postgres

# Redeploy
railway up
```

---

## 📊 Monitoring

### Ver métricas en Railway:
1. Dashboard → Tu proyecto
2. Click en tu servicio
3. Tab "Observability"

**Métricas importantes**:
- CPU Usage < 70%
- Memory Usage < 80%
- Response Time < 2s
- Request Rate

### Logs importantes a buscar:

**✅ Inicio exitoso**:
```
Started SabiApplication in X seconds
```

**❌ Errores críticos**:
```
OutOfMemoryError
Connection refused
Failed to bind
```

---

## 🆘 Última Opción: Soporte

Si nada funciona:

1. **Railway Discord**: https://discord.gg/railway
2. **Railway Docs**: https://docs.railway.app
3. **Stack Overflow**: Etiqueta `railway`

**Al pedir ayuda, incluye**:
- Logs completos (últimas 100 líneas)
- Variables de entorno (sin valores sensibles)
- Versión de Java y Spring Boot
- Descripción del error paso a paso

---

## ✅ Checklist de Verificación

Antes de declarar "no funciona":

- [ ] PostgreSQL está añadido en Railway
- [ ] DATABASE_URL existe en variables
- [ ] SPRING_PROFILES_ACTIVE=prod
- [ ] Build completa sin errores localmente
- [ ] application-prod.properties existe
- [ ] server.port=${PORT:8080} configurado
- [ ] Logs muestran "Started SabiApplication"
- [ ] /health responde 200 OK
- [ ] No hay errors en logs de Railway

---

**Última actualización**: Diciembre 2024
**Versión**: 1.0

