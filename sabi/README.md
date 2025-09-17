# SABI - Plataforma Integral de Entrenamiento Personal

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java" alt="Java 21"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=spring" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue?style=for-the-badge&logo=mysql" alt="MySQL"/>
  <img src="https://img.shields.io/badge/H2-Database-lightgrey?style=for-the-badge&logo=h2" alt="H2 Database"/>
  <img src="https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apache-maven" alt="Maven"/>
</div>

# Proyecto SABI

## Descripción
SABI es una plataforma integral de gestión de entrenamientos y bienestar que conecta clientes con entrenadores personales profesionales. El sistema permite la creación, gestión y seguimiento de rutinas de ejercicio personalizadas, diagnósticos de salud y la administración completa de suscripciones y usuarios.

✨ Características Principales
👥 Sistema Multi-Rol
Clientes: Usuarios que buscan entrenamiento personalizado
Entrenadores: Profesionales que crean y supervisan rutinas
Administradores: Gestión completa del sistema
💪 Gestión de Entrenamientos
Creación de rutinas personalizadas por semanas y días
Biblioteca de ejercicios con videos e instrucciones
Seguimiento de progreso y estadísticas
Asignación de ejercicios con series, repeticiones y pesos
Registro de entrenamientos completados
🏥 Sistema de Diagnósticos
Evaluaciones de salud inicial
Seguimiento de peso, altura y medidas corporales
Historial médico y lesiones previas
Objetivos de entrenamiento personalizados
📊 Panel de Administración
Dashboard con estadísticas en tiempo real
Gestión completa de usuarios
Búsqueda avanzada con múltiples filtros
Reportes y análisis de uso
💳 Gestión de Suscripciones
Múltiples planes de suscripción
Gestión de pagos y renovaciones
Estados: Activa, Pausada, Cancelada, Expirada
Historial de transacciones
🛠️ Stack Tecnológico
Backend
Tecnología	Versión	Propósito
Java	21	Lenguaje principal
Spring Boot	3.x	Framework principal
Spring Security	-	Autenticación y autorización
Spring Data JPA	-	Persistencia de datos
MySQL	8.0+	Base de datos
H2	-	Base de datos en memoria para desarrollo y pruebas
Maven	3.8+	Gestión de dependencias
Lombok	-	Reducción de código boilerplate
Frontend
Tecnología	Propósito
Thymeleaf	Motor de plantillas
HTML5/CSS3	Estructura y estilos
JavaScript	Interactividad
Bootstrap	Framework CSS responsive
Herramientas
Spring Boot DevTools (Hot reload)
Specification API (Consultas dinámicas)
Bean Validation (Validación de datos)
📁 Estructura del Proyecto
SabiSpringSolo/
├── src/main/java/com/app/Sabi/
│   ├── controller/           # Controladores REST y MVC
│   │   ├── AdminController.java
│   │   ├── ClienteController.java
│   │   └── EntrenadorController.java
│   ├── entity/              # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Cliente.java
│   │   ├── Entrenador.java
│   │   ├── Rutina.java
│   │   └── Ejercicio.java
│   ├── dto/                 # Data Transfer Objects
│   ├── repository/          # Repositorios JPA
│   ├── service/             # Interfaces de servicios
│   ├── impl/                # Implementaciones de servicios
│   ├── spec/                # Especificaciones para consultas
│   └── SabiApplication.java # Clase principal
├── src/main/resources/
│   ├── templates/           # Plantillas Thymeleaf
│   │   ├── admin/
│   │   ├── cliente/
│   │   ├── entrenador/
│   │   └── autentificacion/
│   ├── static/              # Recursos estáticos
│   │   ├── css/
│   │   ├── js/
│   │   └── img/
│   └── application.properties
└── README.md
🚀 Instalación y Configuración
Prerequisitos
Java 21 o superior
Maven 3.8+
MySQL 8.0+
IDE compatible (IntelliJ IDEA, Eclipse, VS Code)
1. Clonar el Repositorio
git clone https://github.com/tu-usuario/SabiSpringSolo.git
cd SabiSpringSolo
2. Configurar Base de Datos
CREATE DATABASE sabi_db;
CREATE USER 'sabi_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON sabi_db.* TO 'sabi_user'@'localhost';
FLUSH PRIVILEGES;
3. Configurar application.properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/sabi_db
spring.datasource.username=sabi_user
spring.datasource.password=tu_password

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Servidor
server.port=8080
4. Ejecutar la Aplicación
./mvnw spring-boot:run
5. Acceder al Sistema
URL: http://localhost:8080
Admin por defecto: admin@sabi.com / admin123
📱 Funcionalidades por Rol
🧑‍💼 Para Clientes
🏋️‍♂️ Para Entrenadores
⚙️ Para Administradores
🔄 API Endpoints
Autenticación
POST   /login                    # Iniciar sesión
POST   /registro                 # Crear cuenta
POST   /logout                   # Cerrar sesión
Administración
GET    /admin/dashboard          # Panel principal
GET    /admin/usuarios           # Gestión de usuarios
GET    /admin/usuarios/buscar    # Búsqueda avanzada
POST   /admin/usuarios/{id}/toggle-estado  # Activar/desactivar
Cliente
GET    /cliente/dashboard        # Panel del cliente
GET    /cliente/rutinas          # Ver rutinas asignadas
GET    /cliente/progreso         # Estadísticas de progreso
POST   /cliente/entrenamientos   # Registrar entrenamiento
Entrenador
GET    /entrenador/dashboard     # Panel del entrenador
GET    /entrenador/clientes      # Lista de clientes
POST   /entrenador/rutinas       # Crear rutina
PUT    /entrenador/ejercicios/{id}  # Actualizar ejercicio
🎯 Casos de Uso Principales
📊 Flujo del Cliente
Registro → Crear cuenta con información básica
Diagnóstico → Completar evaluación inicial de salud
Selección → Elegir entrenador según preferencias
Suscripción → Activar plan de entrenamiento
Entrenamiento → Seguir rutinas asignadas
Progreso → Monitorear mejoras y estadísticas
🏋️ Flujo del Entrenador
Perfil → Configurar información profesional
Clientes → Revisar nuevas asignaciones
Evaluación → Analizar diagnósticos de clientes
Rutinas → Crear planes personalizados
Seguimiento → Monitorear progreso de clientes
Ajustes → Modificar rutinas según resultados
🛡️ Flujo Administrativo
Monitoreo → Revisar métricas del sistema
Usuarios → Gestionar cuentas y permisos
Contenido → Moderar ejercicios y rutinas
Reportes → Generar análisis de negocio
Configuración → Ajustar parámetros del sistema
📊 Módulos del Sistema
Módulo	Funcionalidad Principal
🔐 Autenticación	Login/Logout seguro, control de acceso basado en roles
👤 Usuarios	Perfiles detallados, información personal, historial
🏃‍♀️ Entrenamientos	Rutinas, ejercicios, series, progreso, calendario
📈 Analytics	Estadísticas, progreso individual, métricas de engagement
📱 Notificaciones	Alertas en tiempo real, recordatorios, comunicación
🚦 Estados del Sistema
Estados de Usuario
Activo → Puede usar todas las funciones
Inactivo → Acceso restringido
Pendiente → Esperando activación
Suspendido → Temporalmente bloqueado
Estados de Suscripción
Activa → Servicios disponibles
Pausada → Temporalmente suspendida
Cancelada → Finalizada por usuario
Expirada → Venció automáticamente
Pendiente → Esperando pago
Rechazada → Pago fallido
Estados de Rutina
Borrador → En creación
Activa → En ejecución
Pausada → Temporalmente detenida
Completada → Finalizada exitosamente
Cancelada → Terminada prematuramente
📝 Perfiles de Configuración
Perfil	Descripción
dev	Desarrollo local con debugging habilitado
prod	Producción con optimizaciones
test	Configuración para pruebas automatizadas
🤝 Contribución
Fork el proyecto
Crea tu feature branch (git checkout -b feature/AmazingFeature)
Commit tus cambios (git commit -m 'Add some AmazingFeature')
Push al branch (git push origin feature/AmazingFeature)
Abre un Pull Request
📄 Licencia
Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles.

Desarrollado con ❤️ por el equipo SABI

📧 Contacto | 🌐 Website | 📚 Documentación
