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

            helper.setFrom("sabi.gaes5@gmail.com");
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

            helper.setFrom("sabi.gaes5@gmail.com");
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
}

