package com.sabi.sabi.service;

import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarCorreoBienvenida(Usuario usuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(usuario.getEmail());

            if (usuario.getRol() == Rol.CLIENTE) {
                helper.setSubject("¡Bienvenido a SABI - Tu camino hacia el bienestar comienza aquí! 🎯");
                helper.setText(construirMensajeBienvenidaCliente(usuario), true);
            } else if (usuario.getRol() == Rol.ENTRENADOR) {
                helper.setSubject("¡Bienvenido a SABI - Impulsa tu carrera como entrenador! 💪");
                helper.setText(construirMensajeBienvenidaEntrenador(usuario), true);
            }

            mailSender.send(message);
            logger.info("Correo de bienvenida enviado exitosamente a: {}", usuario.getEmail());

        } catch (MessagingException e) {
            logger.error("Error al enviar correo de bienvenida a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendEmail(String asunto, String mensaje, java.util.List<String> destinatarios) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(destinatarios.toArray(new String[0]));
            helper.setSubject(asunto);

            // Crear un HTML básico para el mensaje
            String htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f7fa;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background: white;
                            border-radius: 12px;
                            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #ff7043 0%, #d35400 100%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                        }
                        .content {
                            padding: 30px;
                        }
                        .message {
                            white-space: pre-wrap;
                            font-size: 15px;
                            line-height: 1.8;
                        }
                        .footer {
                            background: #f8fafc;
                            padding: 20px;
                            text-align: center;
                            font-size: 13px;
                            color: #6b7280;
                            border-top: 1px solid #e5e7eb;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>SABI - Salud y Bienestar</h1>
                        </div>
                        <div class="content">
                            <div class="message">{MENSAJE_CONTENIDO}</div>
                        </div>
                        <div class="footer">
                            <p><strong>SABI</strong> - Mensaje de tu entrenador</p>
                            <p style="margin-top: 10px; font-size: 12px; color: #9ca3af;">
                                Este correo fue enviado por tu entrenador personal.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            String htmlMessage = htmlTemplate.replace("{MENSAJE_CONTENIDO}", mensaje.replace("\n", "<br>"));

            helper.setText(htmlMessage, true);

            mailSender.send(message);
            logger.info("Correo enviado exitosamente a {} destinatarios: {}", destinatarios.size(), String.join(", ", destinatarios));

        } catch (MessagingException e) {
            logger.error("Error al enviar correo: {}", e.getMessage());
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

    private String construirMensajeBienvenidaCliente(Usuario usuario) {
        String template = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f7fa;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
                        border-radius: 16px;
                        box-shadow: 0 20px 60px rgba(20,34,61,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #2a7ae4 0%, #1e4e8c 100%);
                        color: white;
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 32px;
                        font-weight: 800;
                        letter-spacing: -0.5px;
                    }
                    .header p {
                        margin: 10px 0 0 0;
                        font-size: 16px;
                        opacity: 0.95;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .greeting {
                        font-size: 20px;
                        font-weight: 700;
                        color: #2a7ae4;
                        margin-bottom: 20px;
                    }
                    .message {
                        font-size: 16px;
                        color: #4a5568;
                        margin-bottom: 25px;
                    }
                    .features {
                        background: linear-gradient(135deg, rgba(42,122,228,0.05), rgba(67,160,71,0.05));
                        border-radius: 12px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    .features h3 {
                        color: #2a7ae4;
                        margin-top: 0;
                        font-size: 18px;
                        font-weight: 700;
                    }
                    .features ul {
                        list-style: none;
                        padding: 0;
                        margin: 15px 0 0 0;
                    }
                    .features li {
                        padding: 10px 0 10px 30px;
                        position: relative;
                        font-size: 15px;
                        color: #4a5568;
                    }
                    .features li:before {
                        content: "✓";
                        position: absolute;
                        left: 0;
                        color: #43a047;
                        font-weight: bold;
                        font-size: 18px;
                    }
                    .cta {
                        text-align: center;
                        margin: 30px 0;
                    }
                    .button {
                        display: inline-block;
                        padding: 15px 40px;
                        background: linear-gradient(90deg, #2a7ae4 0%, #43a047 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 12px;
                        font-weight: 700;
                        font-size: 16px;
                        box-shadow: 0 10px 30px rgba(42,122,228,0.3);
                    }
                    .footer {
                        background: #f8fafc;
                        padding: 25px;
                        text-align: center;
                        font-size: 14px;
                        color: #6b7280;
                        border-top: 1px solid #e5e7eb;
                    }
                    .footer strong {
                        color: #2a7ae4;
                        font-weight: 700;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>SABI</h1>
                        <p>Salud y Bienestar</p>
                    </div>
                    <div class="content">
                        <div class="greeting">¡Hola {NOMBRE_USUARIO}! 👋</div>
                        <div class="message">
                            <p><strong>¡Bienvenido a SABI!</strong> Nos emociona que hayas decidido comenzar tu viaje hacia una vida más saludable y equilibrada.</p>
                            <p>Tu cuenta como <strong>Cliente</strong> ha sido creada exitosamente. Ahora tienes acceso a todas las herramientas que necesitas para transformar tu vida.</p>
                        </div>
                        
                        <div class="features">
                            <h3>¿Qué puedes hacer ahora?</h3>
                            <ul>
                                <li><strong>Completa tu diagnóstico inicial</strong> para que podamos conocerte mejor</li>
                                <li><strong>Explora rutinas personalizadas</strong> diseñadas específicamente para ti</li>
                                <li><strong>Conecta con entrenadores profesionales</strong> certificados</li>
                                <li><strong>Registra tu progreso</strong> y observa tu evolución día a día</li>
                                <li><strong>Accede a tu dashboard personalizado</strong> con métricas y estadísticas</li>
                            </ul>
                        </div>

                        <div class="cta">
                            <a href="http://localhost:8080/auth/login" class="button">Comenzar Ahora</a>
                        </div>

                        <div class="message">
                            <p style="margin-top: 30px; font-size: 15px;">
                                💡 <strong>Tip:</strong> Completa tu perfil y realiza tu primer diagnóstico para obtener recomendaciones precisas y personalizadas.
                            </p>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>SABI</strong> - Tu compañero en salud y bienestar</p>
                        <p>Si tienes alguna pregunta, no dudes en contactarnos.</p>
                        <p style="margin-top: 10px; font-size: 12px; color: #9ca3af;">
                            Este correo fue enviado automáticamente, por favor no respondas a este mensaje.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;
        return template.replace("{NOMBRE_USUARIO}", usuario.getNombre());
    }

    private String construirMensajeBienvenidaEntrenador(Usuario usuario) {
        String template = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f4f7fa;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background: linear-gradient(180deg, #ffffff 0%, #fff8f5 100%);
                        border-radius: 16px;
                        box-shadow: 0 20px 60px rgba(255,112,67,0.15);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #ff7043 0%, #d35400 100%);
                        color: white;
                        padding: 40px 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 32px;
                        font-weight: 800;
                        letter-spacing: -0.5px;
                    }
                    .header p {
                        margin: 10px 0 0 0;
                        font-size: 16px;
                        opacity: 0.95;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .greeting {
                        font-size: 20px;
                        font-weight: 700;
                        color: #ff7043;
                        margin-bottom: 20px;
                    }
                    .message {
                        font-size: 16px;
                        color: #4a5568;
                        margin-bottom: 25px;
                    }
                    .features {
                        background: linear-gradient(135deg, rgba(255,112,67,0.05), rgba(255,193,7,0.05));
                        border-radius: 12px;
                        padding: 25px;
                        margin: 25px 0;
                    }
                    .features h3 {
                        color: #ff7043;
                        margin-top: 0;
                        font-size: 18px;
                        font-weight: 700;
                    }
                    .features ul {
                        list-style: none;
                        padding: 0;
                        margin: 15px 0 0 0;
                    }
                    .features li {
                        padding: 10px 0 10px 30px;
                        position: relative;
                        font-size: 15px;
                        color: #4a5568;
                    }
                    .features li:before {
                        content: "💪";
                        position: absolute;
                        left: 0;
                        font-size: 18px;
                    }
                    .alert-box {
                        background: linear-gradient(135deg, rgba(255,193,7,0.1), rgba(255,112,67,0.05));
                        border-left: 4px solid #fbbf24;
                        border-radius: 8px;
                        padding: 20px;
                        margin: 25px 0;
                    }
                    .alert-box p {
                        margin: 0;
                        font-size: 15px;
                        color: #92400e;
                    }
                    .cta {
                        text-align: center;
                        margin: 30px 0;
                    }
                    .button {
                        display: inline-block;
                        padding: 15px 40px;
                        background: linear-gradient(90deg, #ff7043 0%, #fbbf24 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 12px;
                        font-weight: 700;
                        font-size: 16px;
                        box-shadow: 0 10px 30px rgba(255,112,67,0.3);
                    }
                    .footer {
                        background: #fff8f5;
                        padding: 25px;
                        text-align: center;
                        font-size: 14px;
                        color: #6b7280;
                        border-top: 1px solid #ffedd5;
                    }
                    .footer strong {
                        color: #ff7043;
                        font-weight: 700;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>SABI</h1>
                        <p>Salud y Bienestar</p>
                    </div>
                    <div class="content">
                        <div class="greeting">¡Hola {NOMBRE_USUARIO}! 🏋️</div>
                        <div class="message">
                            <p><strong>¡Bienvenido al equipo de entrenadores de SABI!</strong> Estamos emocionados de que formes parte de nuestra comunidad de profesionales.</p>
                            <p>Tu cuenta como <strong>Entrenador</strong> ha sido creada exitosamente. Ahora puedes empezar a gestionar clientes y crear rutinas personalizadas de manera profesional.</p>
                        </div>

                        <div class="alert-box">
                            <p><strong>⚠️ Importante:</strong> Recuerda completar tu perfil profesional con tu especialidad, años de experiencia y certificaciones para que tus clientes puedan conocerte mejor.</p>
                        </div>
                        
                        <div class="features">
                            <h3>Herramientas disponibles para ti:</h3>
                            <ul>
                                <li><strong>Panel de gestión de clientes</strong> - Administra todos tus clientes desde un solo lugar</li>
                                <li><strong>Creador de rutinas personalizadas</strong> - Diseña planes adaptados a cada cliente</li>
                                <li><strong>Visualización de diagnósticos</strong> - Accede a la información completa de tus clientes</li>
                                <li><strong>Sistema de seguimiento</strong> - Monitorea el progreso y resultados de cada cliente</li>
                                <li><strong>Comunicación directa</strong> - Envía mensajes y notificaciones a tus clientes</li>
                                <li><strong>Estadísticas y métricas</strong> - Analiza el desempeño de tus programas</li>
                            </ul>
                        </div>

                        <div class="cta">
                            <a href="http://localhost:8080/auth/login" class="button">Ir a Mi Dashboard</a>
                        </div>

                        <div class="message">
                            <p style="margin-top: 30px; font-size: 15px;">
                                💡 <strong>Tip profesional:</strong> Un perfil completo y profesional genera más confianza en tus clientes potenciales. Asegúrate de subir tus certificaciones y describir tu especialidad.
                            </p>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>SABI</strong> - Impulsando la excelencia en entrenamiento personal</p>
                        <p>Si necesitas ayuda o tienes preguntas, estamos aquí para apoyarte.</p>
                        <p style="margin-top: 10px; font-size: 12px; color: #9ca3af;">
                            Este correo fue enviado automáticamente, por favor no respondas a este mensaje.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;
        return template.replace("{NOMBRE_USUARIO}", usuario.getNombre());
    }

    /**
     * Envía notificación al entrenador cuando un cliente solicita una suscripción
     */
    @Async
    public void enviarNotificacionSolicitudSuscripcion(String emailEntrenador, String nombreEntrenador, String nombreCliente) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(emailEntrenador);
            helper.setSubject("🔔 Nueva solicitud de entrenamiento - " + nombreCliente);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f7fa;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background: white;
                            border-radius: 16px;
                            box-shadow: 0 20px 60px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                            color: white;
                            padding: 40px 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 800;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .notification-box {
                            background: linear-gradient(135deg, rgba(16,185,129,0.1), rgba(5,150,105,0.05));
                            border-left: 4px solid #10b981;
                            padding: 20px;
                            border-radius: 8px;
                            margin: 20px 0;
                        }
                        .client-name {
                            font-size: 24px;
                            font-weight: 700;
                            color: #10b981;
                            margin: 10px 0;
                        }
                        .cta {
                            text-align: center;
                            margin: 30px 0;
                        }
                        .button {
                            display: inline-block;
                            padding: 15px 40px;
                            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
                            color: white;
                            text-decoration: none;
                            border-radius: 12px;
                            font-weight: 700;
                            font-size: 16px;
                            box-shadow: 0 10px 30px rgba(16,185,129,0.3);
                        }
                        .footer {
                            background: #f0fdf4;
                            padding: 25px;
                            text-align: center;
                            font-size: 14px;
                            color: #6b7280;
                            border-top: 1px solid #d1fae5;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎯 Nueva Oportunidad</h1>
                        </div>
                        <div class="content">
                            <p>Hola <strong>{NOMBRE_ENTRENADOR}</strong>,</p>
                            
                            <div class="notification-box">
                                <p style="margin: 0; font-size: 16px;">¡Tienes una nueva solicitud de entrenamiento!</p>
                                <div class="client-name">{NOMBRE_CLIENTE}</div>
                                <p style="margin: 5px 0 0 0; color: #6b7280;">está interesado en tu servicio de entrenamiento</p>
                            </div>

                            <p style="font-size: 15px;">
                                <strong>Próximos pasos:</strong>
                            </p>
                            <ol style="color: #4b5563;">
                                <li>Revisa el diagnóstico del cliente</li>
                                <li>Envía una cotización con el precio y duración</li>
                                <li>Espera la confirmación del cliente</li>
                            </ol>

                            <div class="cta">
                                <a href="http://localhost:8080/auth/login" class="button">Ver Solicitud Ahora</a>
                            </div>

                            <p style="margin-top: 30px; font-size: 14px; color: #6b7280;">
                                💡 <em>Responde rápido para aumentar tus posibilidades de cerrar la suscripción.</em>
                            </p>
                        </div>
                        <div class="footer">
                            <p><strong>SABI</strong> - Gestión de entrenamiento personal</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            htmlContent = htmlContent.replace("{NOMBRE_ENTRENADOR}", nombreEntrenador)
                                     .replace("{NOMBRE_CLIENTE}", nombreCliente);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Notificación de solicitud de suscripción enviada a: {}", emailEntrenador);

        } catch (MessagingException e) {
            logger.error("Error al enviar notificación de solicitud al entrenador {}: {}", emailEntrenador, e.getMessage());
        }
    }

    /**
     * Envía notificación al cliente cuando el entrenador envía una cotización
     */
    @Async
    public void enviarNotificacionCotizacionRecibida(String emailCliente, String nombreCliente, String nombreEntrenador, Double precio, Integer semanas) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(emailCliente);
            helper.setSubject("💰 Cotización recibida de " + nombreEntrenador);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f7fa;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background: white;
                            border-radius: 16px;
                            box-shadow: 0 20px 60px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
                            color: white;
                            padding: 40px 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                            font-weight: 800;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .price-box {
                            background: linear-gradient(135deg, rgba(59,130,246,0.1), rgba(29,78,216,0.05));
                            border: 2px solid #3b82f6;
                            padding: 30px;
                            border-radius: 12px;
                            text-align: center;
                            margin: 25px 0;
                        }
                        .price {
                            font-size: 48px;
                            font-weight: 800;
                            color: #3b82f6;
                            margin: 10px 0;
                        }
                        .duration {
                            font-size: 20px;
                            color: #6b7280;
                            margin-top: 5px;
                        }
                        .trainer-name {
                            font-size: 22px;
                            font-weight: 700;
                            color: #1f2937;
                            margin: 15px 0;
                        }
                        .cta {
                            text-align: center;
                            margin: 30px 0;
                        }
                        .button {
                            display: inline-block;
                            padding: 15px 40px;
                            background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
                            color: white;
                            text-decoration: none;
                            border-radius: 12px;
                            font-weight: 700;
                            font-size: 16px;
                            box-shadow: 0 10px 30px rgba(59,130,246,0.3);
                        }
                        .footer {
                            background: #eff6ff;
                            padding: 25px;
                            text-align: center;
                            font-size: 14px;
                            color: #6b7280;
                            border-top: 1px solid #dbeafe;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>📋 Cotización Lista</h1>
                        </div>
                        <div class="content">
                            <p>Hola <strong>{NOMBRE_CLIENTE}</strong>,</p>
                            
                            <p style="font-size: 16px;">
                                <strong>{NOMBRE_ENTRENADOR}</strong> ha revisado tu diagnóstico y te ha enviado una cotización personalizada:
                            </p>

                            <div class="price-box">
                                <p style="margin: 0; color: #6b7280; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">Precio Total De</p>
                                <div class="price">${PRECIO}</div>
                                <div class="duration">{SEMANAS} semanas de entrenamiento</div>
                            </div>

                            <p style="font-size: 15px; color: #4b5563;">
                                <strong>¿Qué incluye?</strong><br>
                                ✓ Plan de entrenamiento personalizado<br>
                                ✓ Seguimiento continuo de tu progreso<br>
                                ✓ Rutinas adaptadas a tus objetivos<br>
                                ✓ Apoyo profesional durante todo el programa
                            </p>

                            <div class="cta">
                                <a href="http://localhost:8080/auth/login" class="button" style="color: #ffffff !important; text-decoration: none;">Revisar y Confirmar</a>
                            </div>

                            <p style="margin-top: 30px; font-size: 14px; color: #6b7280;">
                                💡 <em>Puedes aceptar o rechazar esta cotización desde tu panel de cliente.</em>
                            </p>
                        </div>
                        <div class="footer">
                            <p><strong>SABI</strong> - Tu camino hacia el bienestar</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            htmlContent = htmlContent.replace("{NOMBRE_CLIENTE}", nombreCliente)
                                     .replace("{NOMBRE_ENTRENADOR}", nombreEntrenador)
                                     .replace("{PRECIO}", String.format("%.2f", precio))
                                     .replace("{SEMANAS}", semanas.toString());

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Notificación de cotización enviada a: {}", emailCliente);

        } catch (MessagingException e) {
            logger.error("Error al enviar notificación de cotización al cliente {}: {}", emailCliente, e.getMessage());
        }
    }

    /**
     * Envía notificación de fin de rutina
     */
    @Async
    public void enviarNotificacionFinRutina(String emailCliente, String nombreCliente, String nombreEntrenador, String nombreRutina) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(emailCliente);
            helper.setSubject("🎉 ¡Felicitaciones! Has completado tu rutina - " + nombreRutina);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            background-color: #f4f7fa;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background: white;
                            border-radius: 16px;
                            box-shadow: 0 20px 60px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
                            color: white;
                            padding: 40px 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 32px;
                            font-weight: 800;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .congratulations-box {
                            background: linear-gradient(135deg, rgba(245,158,11,0.1), rgba(217,119,6,0.05));
                            border-left: 4px solid #f59e0b;
                            padding: 25px;
                            border-radius: 8px;
                            margin: 25px 0;
                            text-align: center;
                        }
                        .routine-name {
                            font-size: 24px;
                            font-weight: 700;
                            color: #f59e0b;
                            margin: 10px 0;
                        }
                        .cta {
                            text-align: center;
                            margin: 30px 0;
                        }
                        .button {
                            display: inline-block;
                            padding: 15px 40px;
                            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
                            color: white;
                            text-decoration: none;
                            border-radius: 12px;
                            font-weight: 700;
                            font-size: 16px;
                            box-shadow: 0 10px 30px rgba(245,158,11,0.3);
                        }
                        .footer {
                            background: #fffbeb;
                            padding: 25px;
                            text-align: center;
                            font-size: 14px;
                            color: #6b7280;
                            border-top: 1px solid #fef3c7;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🎉 ¡Felicitaciones!</h1>
                        </div>
                        <div class="content">
                            <p>Hola <strong>{NOMBRE_CLIENTE}</strong>,</p>
                            
                            <div class="congratulations-box">
                                <p style="font-size: 18px; margin: 0;">¡Has completado exitosamente tu rutina!</p>
                                <div class="routine-name">{NOMBRE_RUTINA}</div>
                                <p style="margin: 10px 0 0 0; color: #6b7280;">con <strong>{NOMBRE_ENTRENADOR}</strong></p>
                            </div>

                            <p style="font-size: 16px;">
                                🏆 <strong>¡Increíble trabajo!</strong> Has demostrado compromiso y dedicación durante todo este periodo de entrenamiento.
                            </p>

                            <p style="font-size: 15px; color: #4b5563;">
                                <strong>¿Qué sigue ahora?</strong>
                            </p>
                            <ul style="color: #4b5563;">
                                <li>Revisa tu progreso y logros alcanzados</li>
                                <li>Deja una reseña sobre tu experiencia</li>
                                <li>Considera continuar con una nueva rutina</li>
                                <li>Mantén los hábitos saludables que desarrollaste</li>
                            </ul>

                            <div class="cta">
                                <a href="http://localhost:8080/auth/login" class="button">Ver Mi Progreso</a>
                            </div>

                            <p style="margin-top: 30px; font-size: 14px; color: #6b7280; text-align: center;">
                                💪 <em>"El éxito es la suma de pequeños esfuerzos repetidos día tras día"</em>
                            </p>
                        </div>
                        <div class="footer">
                            <p><strong>SABI</strong> - Celebrando tus logros</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            htmlContent = htmlContent.replace("{NOMBRE_CLIENTE}", nombreCliente)
                                     .replace("{NOMBRE_ENTRENADOR}", nombreEntrenador)
                                     .replace("{NOMBRE_RUTINA}", nombreRutina);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Notificación de fin de rutina enviada a: {}", emailCliente);

        } catch (MessagingException e) {
            logger.error("Error al enviar notificación de fin de rutina al cliente {}: {}", emailCliente, e.getMessage());
        }
    }

    @Async
    public void enviarAvisoBloqueo(String emailDestino, String motivo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(emailDestino);
            helper.setSubject("⚠️ Aviso de bloqueo de cuenta en SABI");

            String detalleMotivo = (motivo != null && !motivo.isBlank()) ? motivo : "Incumplimiento de las políticas de uso o actividad inusual.";
            String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset=\"UTF-8\"></head>
                <body style=\"font-family: Segoe UI, Arial, sans-serif; color:#333;\">
                  <div style=\"max-width:600px;margin:20px auto;padding:20px;border:1px solid #eee;border-radius:10px\">
                    <h2 style=\"color:#b91c1c\">Aviso de bloqueo de cuenta</h2>
                    <p>Te informamos que tu cuenta en <strong>SABI</strong> ha sido marcada para bloqueo.</p>
                    <p><strong>Motivo:</strong> %s</p>
                    <p>Si consideras que se trata de un error, contáctanos respondiendo a este correo.</p>
                    <p style=\"margin-top:20px;color:#6b7280\">Este es un mensaje automático. Por favor, no respondas directamente.</p>
                  </div>
                </body>
                </html>
            """.formatted(detalleMotivo);

            helper.setText(html, true);
            mailSender.send(message);
            logger.info("Aviso de bloqueo enviado a {}", emailDestino);
        } catch (MessagingException e) {
            logger.error("Error al enviar aviso de bloqueo a {}: {}", emailDestino, e.getMessage());
        }
    }

    @Async
    public void enviarAvisoVerificacion(String emailDestino) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Sabi.geas5@gmail.com");
            helper.setTo(emailDestino);
            helper.setSubject("✅ Tu cuenta de Entrenador ha sido verificada en SABI");

            String html = """
                <!DOCTYPE html>
                <html>
                <head><meta charset=\"UTF-8\"></head>
                <body style=\"font-family: Segoe UI, Arial, sans-serif; color:#333;\">
                  <div style=\"max-width:600px;margin:20px auto;padding:20px;border:1px solid #eee;border-radius:10px\">
                    <h2 style=\"color:#2861E3\">✅ Cuenta verificada</h2>
                    <p>¡Felicidades! Tu cuenta de <strong>Entrenador</strong> ha sido <strong>verificada</strong> por el equipo de SABI.</p>
                    <p>Desde ahora, los clientes verán una insignia de verificación junto a tu nombre.</p>
                    <div style=\"background:#f0f7ff;border-left:4px solid #2861E3;padding:15px;margin:20px 0;border-radius:5px\">
                      <p style=\"margin:0;color:#1e4ba8\"><strong>Beneficios de la verificación:</strong></p>
                      <ul style=\"margin:10px 0;color:#374151\">
                        <li>Mayor confianza de los clientes</li>
                        <li>Insignia oficial de SABI visible</li>
                        <li>Destaque en resultados de búsqueda</li>
                      </ul>
                    </div>
                    <p style=\"margin-top:20px;color:#6b7280;font-size:0.9em\">Este es un mensaje automático. Por favor, no respondas directamente.</p>
                  </div>
                </body>
                </html>
            """;

            helper.setText(html, true);
            mailSender.send(message);
            logger.info("Aviso de verificación enviado a {}", emailDestino);
        } catch (MessagingException e) {
            logger.error("Error al enviar aviso de verificación a {}: {}", emailDestino, e.getMessage());
        }
    }

    /**
     * Envía un correo al cliente notificando que su entrenador le ha asignado una nueva rutina
     */
    @Async
    public void enviarNotificacionRutinaAsignada(
            String emailCliente,
            String nombreCliente,
            String nombreEntrenador,
            String nombreRutina,
            Integer numeroSemanas,
            String descripcionRutina
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@sabi.com");
            helper.setTo(emailCliente);
            helper.setSubject("🎯 Nueva Rutina Asignada - " + nombreRutina);

            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <style>
                    body { font-family: Arial, sans-serif; background-color: #f3f4f6; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 40px 30px; text-align: center; }
                    .header h1 { color: white; margin: 0; font-size: 28px; }
                    .header .icon { font-size: 60px; margin-bottom: 15px; }
                    .content { padding: 40px 30px; }
                    .content h2 { color: #667eea; margin-top: 0; }
                    .routine-card { background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%); border-left: 4px solid #667eea; padding: 20px; border-radius: 8px; margin: 20px 0; }
                    .routine-card h3 { color: #1f2937; margin: 0 0 15px 0; font-size: 20px; }
                    .routine-info { display: flex; flex-direction: column; gap: 10px; }
                    .info-item { display: flex; align-items: center; color: #4b5563; }
                    .info-item .icon { background: #667eea; color: white; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 12px; font-size: 14px; }
                    .description-box { background: white; border: 2px solid #e5e7eb; padding: 15px; border-radius: 8px; margin-top: 15px; }
                    .cta-button { display: inline-block; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 40px; text-decoration: none; border-radius: 50px; font-weight: bold; margin: 20px 0; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); transition: transform 0.2s; }
                    .cta-button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5); }
                    .trainer-info { background: #f9fafb; padding: 20px; border-radius: 8px; margin: 20px 0; border: 2px solid #e5e7eb; }
                    .trainer-info strong { color: #667eea; }
                    .footer { background: #f9fafb; padding: 20px 30px; text-align: center; color: #6b7280; font-size: 14px; border-top: 1px solid #e5e7eb; }
                    .tips-box { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 15px; border-radius: 8px; margin: 20px 0; }
                    .tips-box strong { color: #92400e; }
                  </style>
                </head>
                <body>
                  <div class="container">
                    <div class="header">
                      <div class="icon">🎯</div>
                      <h1>¡Nueva Rutina Asignada!</h1>
                    </div>
                    <div class="content">
                      <h2>Hola %s,</h2>
                      <p>Tu entrenador <strong>%s</strong> te ha asignado una nueva rutina de entrenamiento personalizada.</p>
                      
                      <div class="routine-card">
                        <h3>📋 %s</h3>
                        <div class="routine-info">
                          <div class="info-item">
                            <div class="icon">📅</div>
                            <span><strong>Duración:</strong> %d semana%s</span>
                          </div>
                          <div class="info-item">
                            <div class="icon">👤</div>
                            <span><strong>Entrenador:</strong> %s</span>
                          </div>
                          <div class="info-item">
                            <div class="icon">⚡</div>
                            <span><strong>Estado:</strong> Lista para comenzar</span>
                          </div>
                        </div>
                        %s
                      </div>
                      
                      <div class="trainer-info">
                        <p style="margin: 0; text-align: center;">
                          <strong>💬 Mensaje de tu entrenador:</strong><br>
                          "Esta rutina ha sido diseñada específicamente para ti, basándose en tus objetivos y nivel actual. ¡Vamos a lograrlo juntos!"
                        </p>
                      </div>
                      
                      <div style="text-align: center;">
                        <a href="http://localhost:8080/cliente/dashboard" class="cta-button">
                          ▶️ Ver Mi Rutina Completa
                        </a>
                      </div>
                      
                      <div class="tips-box">
                        <strong>💡 Consejos para aprovechar al máximo tu rutina:</strong>
                        <ul style="margin: 10px 0 0 0; padding-left: 20px; color: #92400e;">
                          <li>Revisa la rutina completa antes de comenzar</li>
                          <li>Consulta con tu entrenador si tienes dudas</li>
                          <li>Sigue las indicaciones de series y repeticiones</li>
                          <li>Registra tu progreso después de cada sesión</li>
                          <li>Mantén comunicación constante con tu entrenador</li>
                        </ul>
                      </div>
                      
                      <p style="color: #6b7280; font-size: 14px; margin-top: 30px;">
                        <strong>Nota:</strong> Puedes ver todos los detalles de tu rutina, incluyendo ejercicios, series, repeticiones y técnicas, desde tu dashboard de cliente.
                      </p>
                    </div>
                    <div class="footer">
                      <p style="margin: 5px 0;">Sistema de Asistencia para el Bienestar Integral</p>
                      <p style="margin: 5px 0;">📧 Este es un correo automático, por favor no respondas directamente.</p>
                      <p style="margin: 5px 0; color: #9ca3af;">© 2025 SABI - Todos los derechos reservados</p>
                    </div>
                  </div>
                </body>
                </html>
            """.formatted(
                    nombreCliente,
                    nombreEntrenador,
                    nombreRutina,
                    numeroSemanas,
                    numeroSemanas == 1 ? "" : "s",
                    nombreEntrenador,
                    descripcionRutina != null && !descripcionRutina.isEmpty()
                            ? "<div class=\"description-box\"><strong>📝 Descripción:</strong><br>" + descripcionRutina + "</div>"
                            : ""
            );

            helper.setText(html, true);
            mailSender.send(message);

            logger.info("✅ Correo de rutina asignada enviado a {} por el entrenador {}", emailCliente, nombreEntrenador);
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║  📧 CORREO DE RUTINA ASIGNADA ENVIADO");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.println("  👤 Cliente: " + nombreCliente + " (" + emailCliente + ")");
            System.out.println("  🏋️ Entrenador: " + nombreEntrenador);
            System.out.println("  📋 Rutina: " + nombreRutina);
            System.out.println("  📅 Semanas: " + numeroSemanas);
            System.out.println("╚════════════════════════════════════════════════════════╝");

        } catch (MessagingException e) {
            logger.error("❌ Error al enviar correo de rutina asignada a {}: {}", emailCliente, e.getMessage());
            System.err.println("❌ ERROR enviando correo de rutina: " + e.getMessage());
        }
    }
}
