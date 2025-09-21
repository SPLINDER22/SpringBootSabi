# SABI - Salud Y Bienestar 

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
Clientes: Usuarios que buscan entrenamiento personalizado y gestionan su progreso
Entrenadores: Profesionales que crean, asignan y supervisan rutinas
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
sabi/
├── src/main/java/com/sabi/sabi/
│   ├── controller/           # Controladores REST y MVC
│   │   ├── ClienteController.java
│   │   └── EntrenadorController.java
│   ├── entity/              # Entidades JPA
│   ├── dto/                 # Data Transfer Objects
│   ├── repository/          # Repositorios JPA
│   ├── service/             # Interfaces de servicios
│   ├── impl/                # Implementaciones de servicios
│   ├── security/            # Seguridad y autenticación
│   ├── config/              # Configuración general
│   └── SabiApplication.java # Clase principal
├── src/main/resources/
│   ├── templates/           # Plantillas Thymeleaf
│   │   ├── auth/
│   │   ├── cliente/
│   │   ├── entrenador/
│   │   ├── dias/
│   │   ├── ejercicios/
│   │   ├── ejercicios-asignados/
│   │   ├── rutinas/
│   │   ├── semanas/
│   │   ├── series/
│   │   ├── suscripciones/
│   │   ├── fragments/
│   │   ├── layout/
│   │   └── error.html, index.html
│   ├── static/              # Recursos estáticos
│   │   ├── css/
│   │   ├── js/
│   │   ├── img/
│   │   └── vendor/
│   ├── application.properties
│   ├── application-h2.properties
│   └── application-mysql.properties
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
📱 Funcionalidades por Rol
🧑‍💼 Para Clientes
- Registro y autenticación
- Diagnóstico inicial y seguimiento de salud
- Selección y solicitud de entrenador
- Visualización y seguimiento de rutinas asignadas
- Registro de entrenamientos y progreso

🏋️‍♂️ Para Entrenadores
- Gestión de perfil profesional
- Visualización de diagnósticos de clientes
- Creación y asignación de rutinas personalizadas
- Seguimiento del progreso de clientes

🔄 API Endpoints
Autenticación
POST   /auth/login                    # Iniciar sesión
POST   /auth/registro                 # Crear cuenta
POST   /auth/logout                   # Cerrar sesión

Cliente
GET    /cliente/dashboard             # Panel del cliente
GET    /cliente/rutinas               # Ver rutinas asignadas
GET    /cliente/listaEntrenadores     # Ver lista de entrenadores
GET    /cliente/diagnostico/historial # Historial de diagnósticos
POST   /cliente/diagnostico/guardar   # Guardar diagnóstico
POST   /cliente/entrenamientos        # Registrar entrenamiento

Entrenador
GET    /entrenador/dashboard          # Panel del entrenador
GET    /entrenadores                  # Lista de entrenadores activos
POST   /entrenadores/solicitar/{id}   # Solicitar entrenador (cliente)
POST   /entrenador/rutinas            # Crear rutina
PUT    /entrenador/ejercicios/{id}    # Actualizar ejercicio
🎯 Casos de Uso Principales

📥 <b>Exportación de Datos</b>
El sistema permite descargar diagnósticos, clientes y rutinas en formato <b>PDF</b> o <b>Excel</b> gracias a la integración de dependencias especializadas para generación de archivos. Esta funcionalidad facilita la gestión y respaldo de información relevante para usuarios y entrenadores.
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
 Módulos del Sistema
Módulo	Funcionalidad Principal
🔐 Autenticación	Login/Logout seguro, control de acceso basado en roles (Cliente, Entrenador)
👤 Usuarios	Perfiles detallados, información personal, historial
🏃‍♀️ Entrenamientos	Rutinas, ejercicios, series, progreso, calendario
📈 Analytics	Estadísticas y progreso individual
📱 Notificaciones	Alertas y recordatorios
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


<div align="center">
  <b>Desarrollado con ❤️ por el equipo SABI</b>
  <br><br>
  <span style="color:#0074D9; font-weight:bold;">William Espinel</span><br>
  <span style="color:#FFDC00; font-weight:bold;">Andres Mena</span><br>
  <span style="color:#2ECC40; font-weight:bold;">Santiago Castro</span>
</div>

📧 Contacto | 🌐 Website | 📚 Documentación
