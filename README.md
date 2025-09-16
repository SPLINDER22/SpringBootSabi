📋 Descripción
SABI es una plataforma integral de gestión de entrenamientos y bienestar que conecta a clientes con entrenadores personales profesionales. El sistema permite la creación, gestión y seguimiento de rutinas de ejercicio personalizadas, diagnósticos de salud, y la administración completa de suscripciones y usuarios.

🌟 Características Principales
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
Historial de cambios y progreso
📊 Panel de Administración
Dashboard con estadísticas en tiempo real
Gestión completa de usuarios
Búsqueda avanzada con múltiples filtros
Reportes y análisis de uso
Notificaciones del sistema
💳 Gestión de Suscripciones
Múltiples planes de suscripción
Gestión de pagos y renovaciones
Estados: Activa, Pausada, Cancelada, Expirada
Historial de transacciones
🛠️ Tecnologías Utilizadas
Backend
Java 21 - Lenguaje principal
Spring Boot 3.x - Framework principal
Spring Security - Autenticación y autorización
Spring Data JPA - Persistencia de datos
MySQL - Base de datos principal
Lombok - Reducción de código boilerplate
Maven - Gestión de dependencias
Frontend
Thymeleaf - Motor de plantillas
HTML5/CSS3 - Estructura y estilos
JavaScript - Interactividad
Bootstrap - Framework CSS responsive
Herramientas y Utilidades
Spring Boot DevTools - Desarrollo y hot reload
Specification API - Consultas dinámicas
Bean Validation - Validación de datos
📁 Estructura del Proyecto
SabiSpringSolo/
├── src/main/java/com/app/Sabi/
│   ├── controller/          # Controladores REST y MVC
│   │   ├── AdminController.java
│   │   ├── ClienteController.java
│   │   └── EntrenadorController.java
│   ├── entity/              # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Cliente.java
│   │   ├── Entrenador.java
│   │   ├── Rutina.java
│   │   ├── Ejercicio.java
│   │   └── ...
│   ├── dto/                 # Data Transfer Objects
│   │   ├── UsuarioDTO.java
│   │   ├── RutinaDTO.java
│   │   └── ...
│   ├── repository/          # Repositorios JPA
│   ├── service/            # Interfaces de servicios
│   ├── impl/               # Implementaciones de servicios
│   ├── spec/               # Especificaciones para consultas
│   └── SabiApplication.java
├── src/main/resources/
│   ├── templates/          # Plantillas Thymeleaf
│   │   ├── admin/
│   │   ├── cliente/
│   │   ├── entrenador/
│   │   └── autentificacion/
│   ├── static/             # Recursos estáticos
│   │   ├── css/
│   │   ├── js/
│   │   └── img/
│   └── application.properties
└── README.md
🚀 Funcionalidades Detalladas
Para Clientes
✅ Registro y creación de perfil personalizado
✅ Búsqueda y selección de entrenadores
✅ Visualización de rutinas asignadas
✅ Seguimiento de entrenamientos diarios
✅ Registro de progreso y estadísticas
✅ Gestión de diagnósticos de salud
✅ Sistema de notificaciones
✅ Historial de entrenamientos
Para Entrenadores
✅ Perfil profesional con certificaciones
✅ Gestión de clientes asignados
✅ Creación de rutinas personalizadas
✅ Biblioteca de ejercicios personalizable
✅ Seguimiento del progreso de clientes
✅ Sistema de calificaciones y reseñas
✅ Dashboard con estadísticas de clientes
✅ Gestión de horarios y disponibilidad
Para Administradores
✅ Dashboard ejecutivo con KPIs
✅ Gestión completa de usuarios
✅ Búsqueda avanzada con filtros múltiples
✅ Control de suscripciones y pagos
✅ Generación de reportes
✅ Gestión de contenido y ejercicios
✅ Moderación de calificaciones
✅ Configuración del sistema
📊 Módulos del Sistema
🔐 Autenticación y Seguridad
Login/Logout seguro
Registro de usuarios con validación
Control de acceso basado en roles
Sesiones seguras
Protección CSRF
👤 Gestión de Usuarios
Perfiles detallados por tipo de usuario
Información personal y de contacto
Historial de actividad
Estados de cuenta (activo/inactivo)
Recuperación de contraseñas
🏃‍♀️ Sistema de Entrenamientos
Rutinas: Organizadas por semanas y días
Ejercicios: Biblioteca completa con instrucciones
Series: Control detallado de repeticiones y peso
Progreso: Seguimiento automático de mejoras
Calendario: Planificación de entrenamientos
📈 Analytics y Reportes
Estadísticas de uso del sistema
Progreso individual de clientes
Rendimiento de entrenadores
Métricas de engagement
Reportes financieros
📱 Sistema de Notificaciones
Notificaciones en tiempo real
Recordatorios de entrenamientos
Alertas de progreso
Comunicación entre usuarios
Estados de lectura
⚙️ Configuración y Despliegue
Requisitos Previos
Java 21 o superior
Maven 3.8+
MySQL 8.0+
IDE compatible (IntelliJ IDEA, Eclipse, VS Code)
Instalación Local
Clonar el repositorio
git clone https://github.com/tu-usuario/SabiSpringSolo.git
cd SabiSpringSolo
Configurar la base de datos
CREATE DATABASE sabi_db;
CREATE USER 'sabi_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON sabi_db.* TO 'sabi_user'@'localhost';
Configurar application.properties
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
Ejecutar la aplicación
./mvnw spring-boot:run
Acceder al sistema
URL: http://localhost:8080
Admin por defecto: admin@sabi.com / admin123
Perfiles de Configuración
dev: Desarrollo local con debugging
prod: Producción con optimizaciones
test: Pruebas automatizadas
🔄 API Endpoints
Autenticación
POST /login - Iniciar sesión
POST /registro - Crear cuenta
POST /logout - Cerrar sesión
Administración
GET /admin/dashboard - Panel principal
GET /admin/usuarios - Gestión de usuarios
GET /admin/usuarios/buscar - Búsqueda avanzada
POST /admin/usuarios/{id}/toggle-estado - Activar/desactivar
Cliente
GET /cliente/dashboard - Panel del cliente
GET /cliente/rutinas - Ver rutinas asignadas
GET /cliente/progreso - Estadísticas de progreso
POST /cliente/entrenamientos - Registrar entrenamiento
Entrenador
GET /entrenador/dashboard - Panel del entrenador
GET /entrenador/clientes - Lista de clientes
POST /entrenador/rutinas - Crear rutina
PUT /entrenador/ejercicios/{id} - Actualizar ejercicio
🎯 Casos de Uso Principales
Flujo del Cliente
Registro: Crear cuenta con información básica
Diagnóstico: Completar evaluación inicial de salud
Selección: Elegir entrenador según preferencias
Suscripción: Activar plan de entrenamiento
Entrenamiento: Seguir rutinas asignadas
Progreso: Monitorear mejoras y estadísticas
Flujo del Entrenador
Perfil: Configurar información profesional
Clientes: Revisar nuevas asignaciones
Evaluación: Analizar diagnósticos de clientes
Rutinas: Crear planes personalizados
Seguimiento: Monitorear progreso de clientes
Ajustes: Modificar rutinas según resultados
Flujo Administrativo
Monitoreo: Revisar métricas del sistema
Usuarios: Gestionar cuentas y permisos
Contenido: Moderar ejercicios y rutinas
Reportes: Generar análisis de negocio
Configuración: Ajustar parámetros del sistema
🚦 Estados y Flujos de Datos
Estados de Usuario
Activo: Puede usar todas las funciones
Inactivo: Acceso restringido
Pendiente: Esperando activación
Suspendido: Temporalmente bloqueado
Estados de Suscripción
Activa: Servicios disponibles
Pausada: Temporalmente suspendida
Cancelada: Finalizada por usuario
Expirada: Venció automáticamente
Pendiente: Esperando pago
Rechazada: Pago fallido
Estados de Rutina
Borrador: En creación
Activa: En ejecución
Pausada: Temporalmente detenida
Completada: Finalizada exitosamente
Cancelada: Terminada prematuramente
