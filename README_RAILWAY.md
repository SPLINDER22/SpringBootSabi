# 🏋️ SABI - Sistema de Salud y Bienestar

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/PostgreSQL-15+-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Railway-Ready-purple?style=for-the-badge&logo=railway" alt="Railway"/>
</div>

---

## 🚀 ¡LISTO PARA RAILWAY!

Este proyecto está **100% configurado** para despliegue en Railway. Ver guías:

📚 **[RAILWAY_DEPLOYMENT.md](sabi/RAILWAY_DEPLOYMENT.md)** - Guía completa paso a paso  
📊 **[RESUMEN_RAILWAY.md](sabi/RESUMEN_RAILWAY.md)** - Resumen ejecutivo  
🔐 **[ENVIRONMENT_VARIABLES.md](sabi/ENVIRONMENT_VARIABLES.md)** - Variables necesarias  
🐛 **[TROUBLESHOOTING.md](sabi/TROUBLESHOOTING.md)** - Solución de problemas  
📸 **[CLOUDINARY_GUIDE.md](sabi/CLOUDINARY_GUIDE.md)** - Almacenamiento de imágenes  

---

## 📋 Descripción

**SABI** es una plataforma integral que conecta **clientes** con **entrenadores certificados** para gestionar entrenamientos personalizados, diagnósticos de salud y seguimiento de progreso.

### 🎯 Características Principales

- 👥 **Gestión de Usuarios**: Clientes, Entrenadores y Administradores
- 📊 **Diagnósticos Personalizados**: IMC, peso, altura, sueño y dieta
- 💪 **Rutinas de Entrenamiento**: Creación y asignación personalizada
- ✅ **Seguimiento de Progreso**: Registro de series y ejercicios completados
- 📧 **Sistema de Mensajería**: Comunicación directa entrenador-cliente
- 🎓 **Verificación de Entrenadores**: Sistema de certificaciones
- 💳 **Gestión de Suscripciones**: Planes y pagos
- 📈 **Reportes y Analytics**: Exportación a Excel y PDF

---

## 💻 Tecnologías

### Backend
- **Spring Boot 3.5.5** - Framework principal
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - ORM
- **PostgreSQL** - Base de datos producción
- **H2** - Base de datos desarrollo
- **Maven** - Gestión de dependencias

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap 5** - Framework CSS
- **JavaScript** - Interactividad
- **SB Admin 2** - Template administrativo

### Servicios
- **Railway** - Hosting y base de datos
- **Cloudinary** - Almacenamiento de imágenes (recomendado)
- **Gmail SMTP** - Envío de correos

---

## 🚀 Quick Start

### Desarrollo Local

```bash
# Clonar repositorio
git clone https://github.com/TU_USUARIO/sabi-app.git
cd sabi-app/sabi

# Compilar
mvn clean install

# Ejecutar (modo H2)
mvn spring-boot:run

# O ejecutar (modo MySQL)
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Accede a: **http://localhost:8080**

### Usuarios de Prueba

| Rol | Email | Password |
|-----|-------|----------|
| Admin | `admin@sabi.com` | `admin123` |
| Entrenador | `juan.perez@sabi.com` | `entrenador123` |
| Cliente | `carlos.garcia@sabi.com` | `cliente123` |

---

## 🚢 Despliegue en Railway

### 1. Verificar Configuración

```powershell
cd sabi
.\verify-deploy.ps1
```

### 2. Subir a GitHub

```bash
git init
git add .
git commit -m "Ready for Railway"
git remote add origin https://github.com/TU_USUARIO/sabi-app.git
git push -u origin main
```

### 3. Desplegar en Railway

1. **Crear proyecto**: https://railway.app/dashboard
2. **Deploy from GitHub repo**: Selecciona tu repo
3. **Add PostgreSQL**: New → Database → PostgreSQL
4. **Configurar variables**: Ver [ENVIRONMENT_VARIABLES.md](sabi/ENVIRONMENT_VARIABLES.md)

**Variables obligatorias**:
```env
SPRING_PROFILES_ACTIVE=prod
JAVA_OPTS=-Xmx512m -Xms256m
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_app_password
UPLOAD_PATH=/tmp/uploads/perfiles
UPLOAD_DIAGNOSTICOS_PATH=/tmp/uploads/diagnosticos
```

5. **Deploy automático** ✅

Ver guía completa: **[RAILWAY_DEPLOYMENT.md](sabi/RAILWAY_DEPLOYMENT.md)**

---

## 📊 Características por Rol

### 👨‍💼 Administrador
- ✅ Gestión completa de usuarios
- ✅ Verificación de entrenadores
- ✅ Dashboard con métricas
- ✅ Exportación de reportes (Excel/PDF)
- ✅ Gestión de suscripciones

### 💪 Entrenador
- ✅ Gestión de clientes
- ✅ Creación de rutinas personalizadas
- ✅ Seguimiento de progreso
- ✅ Mensajería con clientes
- ✅ Subida de certificaciones
- ✅ Historial de diagnósticos

### 🏃 Cliente
- ✅ Registro de diagnósticos
- ✅ Visualización de rutinas
- ✅ Registro de ejercicios
- ✅ Mensajería con entrenador
- ✅ Seguimiento de IMC
- ✅ Selección de entrenador verificado

---

## 📁 Estructura del Proyecto

```
sabi/
├── src/main/
│   ├── java/com/sabi/sabi/
│   │   ├── config/          # Configuraciones
│   │   ├── controller/      # Controladores MVC
│   │   ├── dto/             # DTOs
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositorios
│   │   ├── security/        # Seguridad
│   │   └── service/         # Lógica de negocio
│   └── resources/
│       ├── application-prod.properties  # Producción
│       ├── static/          # CSS, JS
│       └── templates/       # Vistas Thymeleaf
├── Procfile                 # Railway
├── railway.json             # Railway config
├── nixpacks.toml           # Build
├── RAILWAY_DEPLOYMENT.md    # Guía despliegue
└── pom.xml                  # Maven
```

---

## 💰 Costos Estimados

| Servicio | Plan | Costo/mes |
|----------|------|-----------|
| Railway | Starter | **$0** (con créditos) |
| Railway | Developer | $20 |
| Cloudinary | Free | **$0** |
| AWS S3 | Free Tier | $1-2 |
| **TOTAL** | | **$0-22/mes** |

---

## 🔐 Seguridad

- ✅ Contraseñas con BCrypt
- ✅ Protección CSRF
- ✅ Validación server-side
- ✅ Control de acceso por roles
- ✅ HTTPS en producción

---

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Con cobertura
mvn test jacoco:report
```

---

## 📈 Roadmap

### v1.1 (Corto plazo)
- [ ] Cloudinary implementado
- [ ] PWA (Progressive Web App)
- [ ] Notificaciones push

### v2.0 (Mediano plazo)
- [ ] App móvil nativa
- [ ] Integración wearables
- [ ] Pasarela de pagos

### v3.0 (Largo plazo)
- [ ] IA para recomendaciones
- [ ] Marketplace de entrenadores
- [ ] Gamificación

---

## 🐛 Troubleshooting

Ver guía completa: **[TROUBLESHOOTING.md](sabi/TROUBLESHOOTING.md)**

### Problemas comunes:

**Error de base de datos**:
```bash
# Verificar que PostgreSQL esté añadido en Railway
# Verificar SPRING_PROFILES_ACTIVE=prod
```

**Archivos desaparecen**:
```bash
# Railway usa almacenamiento efímero
# Solución: Implementar Cloudinary
# Ver: CLOUDINARY_GUIDE.md
```

**Out of Memory**:
```bash
# Ajustar JAVA_OPTS en Railway:
JAVA_OPTS=-Xmx400m -Xms200m
```

---

## 📚 Documentación

- **[RAILWAY_DEPLOYMENT.md](sabi/RAILWAY_DEPLOYMENT.md)** - Despliegue completo
- **[RESUMEN_RAILWAY.md](sabi/RESUMEN_RAILWAY.md)** - Resumen ejecutivo
- **[ENVIRONMENT_VARIABLES.md](sabi/ENVIRONMENT_VARIABLES.md)** - Variables de entorno
- **[TROUBLESHOOTING.md](sabi/TROUBLESHOOTING.md)** - Solución de problemas
- **[CLOUDINARY_GUIDE.md](sabi/CLOUDINARY_GUIDE.md)** - Almacenamiento en la nube

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crea tu rama: `git checkout -b feature/NuevaCaracteristica`
3. Commit: `git commit -m 'Add: Nueva característica'`
4. Push: `git push origin feature/NuevaCaracteristica`
5. Pull Request

---

## 📝 Licencia

Proyecto privado y confidencial.

---

## 📞 Soporte

- **Email**: Sabi.geas5@gmail.com
- **Documentación**: Ver carpeta `/sabi/docs`

---

## 🙏 Agradecimientos

- Spring Boot Team
- Railway.app
- Cloudinary
- Bootstrap
- SB Admin 2

---

<div align="center">
  <strong>Versión 1.0.0</strong> | <strong>Diciembre 2024</strong> | <strong>✅ Listo para Producción</strong>
</div>

---

## 🔗 Enlaces Útiles

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Railway Docs](https://docs.railway.app)
- [Cloudinary Docs](https://cloudinary.com/documentation)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)

---

**¡Gracias por usar Sabi!** 💪🏋️‍♀️🏃‍♂️

