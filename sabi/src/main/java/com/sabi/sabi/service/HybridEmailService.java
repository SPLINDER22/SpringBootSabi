package com.sabi.sabi.service;

import com.sabi.sabi.entity.Usuario;
import com.sabi.sabi.entity.enums.Rol;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio híbrido de email que usa:
 * 1. SendGrid como prioridad (mejor deliverability)
 * 2. Gmail SMTP como fallback
 */
@Service
public class HybridEmailService {

    private static final Logger logger = LoggerFactory.getLogger(HybridEmailService.class);

    @Autowired(required = false)
    private SendGridEmailService sendGridService;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:Sabi.geas5@gmail.com}")
    private String fromEmail;

    public HybridEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía email de bienvenida al registrarse
     */
    @Async
    public void enviarCorreoBienvenida(Usuario usuario) {
        String subject;
        String htmlContent;

        if (usuario.getRol() == Rol.CLIENTE) {
            subject = "¡Bienvenido a SABI - Tu camino hacia el bienestar comienza aquí! 🎯";
            htmlContent = construirMensajeBienvenidaCliente(usuario);
        } else if (usuario.getRol() == Rol.ENTRENADOR) {
            subject = "¡Bienvenido a SABI - Impulsa tu carrera como entrenador! 💪";
            htmlContent = construirMensajeBienvenidaEntrenador(usuario);
        } else {
            logger.warn("Rol desconocido para usuario: {}", usuario.getEmail());
            return;
        }

        enviarCorreo(usuario.getEmail(), subject, htmlContent);
    }

    /**
     * Envía email a múltiples destinatarios
     */
    @Async
    public void sendEmail(String asunto, String mensaje, List<String> destinatarios) {
        String htmlContent = construirMensajeGenerico(asunto, mensaje);

        // Enviar a cada destinatario
        for (String email : destinatarios) {
            enviarCorreo(email, asunto, htmlContent);
        }
    }

    /**
     * Método principal que decide qué servicio usar
     */
    private void enviarCorreo(String toEmail, String subject, String htmlContent) {
        // Intentar con SendGrid primero si está configurado
        if (sendGridService != null && sendGridService.isConfigured()) {
            logger.info("📧 Usando SendGrid para enviar email a: {}", toEmail);
            sendGridService.sendEmail(toEmail, subject, htmlContent);
        } else {
            // Fallback a Gmail SMTP
            logger.info("📧 Usando Gmail SMTP para enviar email a: {}", toEmail);
            enviarConGmailSMTP(toEmail, subject, htmlContent);
        }
    }

    /**
     * Envío mediante Gmail SMTP (fallback)
     */
    private void enviarConGmailSMTP(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("✅ Email enviado exitosamente a: {} (Gmail SMTP)", toEmail);

        } catch (MessagingException e) {
            logger.error("❌ Error al enviar correo a {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado al enviar correo a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Construye mensaje genérico con template HTML
     */
    private String construirMensajeGenerico(String titulo, String mensaje) {
        return String.format("""
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
                        background: linear-gradient(135deg, #2a7ae4 0%, #1e4e8c 100%);
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
                        <h1>%s</h1>
                    </div>
                    <div class="content">
                        <div class="message">%s</div>
                    </div>
                    <div class="footer">
                        <p><strong>SABI</strong> - Salud y Bienestar</p>
                        <p style="margin-top: 10px; font-size: 12px; color: #9ca3af;">
                            Este correo fue enviado desde la plataforma SABI
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, titulo, mensaje.replace("\n", "<br>"));
    }

    private String construirMensajeBienvenidaCliente(Usuario usuario) {
        return String.format("""
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
                        background: linear-gradient(135deg, #2a7ae4 0%, #1e4e8c 100%);
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
                    .cta-button {
                        display: inline-block;
                        padding: 14px 32px;
                        background: linear-gradient(135deg, #2a7ae4, #1e4e8c);
                        color: white;
                        text-decoration: none;
                        border-radius: 8px;
                        font-weight: 600;
                        margin: 20px 0;
                    }
                    .footer {
                        background: #f8fafc;
                        padding: 20px;
                        text-align: center;
                        font-size: 13px;
                        color: #6b7280;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎯 ¡Bienvenido a SABI!</h1>
                        <p>Tu plataforma de entrenamiento personal</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Hola %s,</div>
                        <div class="message">
                            ¡Estamos emocionados de tenerte con nosotros! 🎉
                            <br><br>
                            Has dado el primer paso hacia una vida más saludable y activa.
                            <br><br>
                            En SABI podrás:
                            <ul>
                                <li>✅ Completar tu diagnóstico personalizado</li>
                                <li>✅ Conectarte con entrenadores certificados</li>
                                <li>✅ Recibir rutinas adaptadas a tus objetivos</li>
                                <li>✅ Hacer seguimiento de tu progreso</li>
                            </ul>
                        </div>
                        <a href="https://sabi.up.railway.app/cliente/dashboard" class="cta-button">
                            Ir a mi Dashboard
                        </a>
                    </div>
                    <div class="footer">
                        <p><strong>SABI</strong> - Salud y Bienestar Inteligente</p>
                        <p>© 2024 SABI. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """, usuario.getNombre());
    }

    private String construirMensajeBienvenidaEntrenador(Usuario usuario) {
        return String.format("""
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
                        background: linear-gradient(135deg, #ff7043 0%, #d35400 100%);
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
                    .cta-button {
                        display: inline-block;
                        padding: 14px 32px;
                        background: linear-gradient(135deg, #ff7043, #d35400);
                        color: white;
                        text-decoration: none;
                        border-radius: 8px;
                        font-weight: 600;
                        margin: 20px 0;
                    }
                    .footer {
                        background: #f8fafc;
                        padding: 20px;
                        text-align: center;
                        font-size: 13px;
                        color: #6b7280;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>💪 ¡Bienvenido a SABI!</h1>
                        <p>Plataforma para entrenadores profesionales</p>
                    </div>
                    <div class="content">
                        <div class="greeting">Hola %s,</div>
                        <div class="message">
                            ¡Bienvenido al equipo de entrenadores SABI! 🎉
                            <br><br>
                            Estamos felices de que te unas a nuestra plataforma.
                            <br><br>
                            Como entrenador en SABI podrás:
                            <ul>
                                <li>✅ Gestionar tus clientes de forma eficiente</li>
                                <li>✅ Crear rutinas personalizadas</li>
                                <li>✅ Hacer seguimiento del progreso</li>
                                <li>✅ Comunicarte directamente con tus clientes</li>
                            </ul>
                            <br>
                            <strong>Próximo paso:</strong> Completa tu perfil de entrenador para que los clientes puedan encontrarte.
                        </div>
                        <a href="https://sabi.up.railway.app/entrenador/dashboard" class="cta-button">
                            Ir a mi Dashboard
                        </a>
                    </div>
                    <div class="footer">
                        <p><strong>SABI</strong> - Salud y Bienestar Inteligente</p>
                        <p>© 2024 SABI. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """, usuario.getNombre());
    }

    /**
     * Verifica si el servicio de email está configurado
     */
    public boolean isConfigured() {
        if (sendGridService != null && sendGridService.isConfigured()) {
            logger.info("✅ SendGrid configurado y listo");
            return true;
        }

        // Verificar si Gmail SMTP está configurado
        boolean gmailConfigured = fromEmail != null && !fromEmail.isBlank();
        if (gmailConfigured) {
            logger.info("✅ Gmail SMTP configurado como fallback");
        }

        return gmailConfigured;
    }
}

