package com.sabi.sabi.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de envío de correos usando SendGrid API
 * Requiere: SENDGRID_API_KEY en variables de entorno
 *
 * SendGrid ofrece 100 correos/día GRATIS
 * Mejor deliverability que Gmail SMTP
 */
@Service
public class SendGridEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    @Value("${sendgrid.api.key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email:Sabi.geas5@gmail.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:SABI - Salud y Bienestar}")
    private String fromName;

    /**
     * Envía un email simple con contenido HTML
     */
    @Async
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        if (!isConfigured()) {
            logger.warn("⚠️ SendGrid no configurado. Email no enviado a: {}", toEmail);
            return;
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("✅ Email enviado exitosamente a: {} - Status: {}", toEmail, response.getStatusCode());
            } else {
                logger.error("❌ Error al enviar email a: {} - Status: {} - Body: {}",
                    toEmail, response.getStatusCode(), response.getBody());
            }

        } catch (IOException e) {
            logger.error("❌ Error de IO al enviar email a {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado al enviar email a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Envía email a múltiples destinatarios
     */
    @Async
    public void sendBulkEmail(List<String> toEmails, String subject, String htmlContent) {
        if (!isConfigured()) {
            logger.warn("⚠️ SendGrid no configurado. Emails no enviados");
            return;
        }

        for (String email : toEmails) {
            sendEmail(email, subject, htmlContent);
        }
    }

    /**
     * Envía email con plantilla HTML personalizada
     */
    @Async
    public void sendTemplatedEmail(String toEmail, String toName, String subject, String htmlContent) {
        if (!isConfigured()) {
            logger.warn("⚠️ SendGrid no configurado. Email no enviado a: {}", toEmail);
            return;
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail, toName);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);

            // Personalización adicional
            Personalization personalization = new Personalization();
            personalization.addTo(to);
            personalization.addSubstitution("%name%", toName);
            mail.addPersonalization(personalization);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("✅ Email templado enviado a: {} - Status: {}", toEmail, response.getStatusCode());
            } else {
                logger.error("❌ Error al enviar email templado a: {} - Status: {}", toEmail, response.getStatusCode());
            }

        } catch (IOException e) {
            logger.error("❌ Error de IO al enviar email a {}: {}", toEmail, e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error al enviar email a {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Verifica si SendGrid está configurado
     */
    public boolean isConfigured() {
        boolean configured = sendGridApiKey != null && !sendGridApiKey.isBlank() && !sendGridApiKey.equals("your-sendgrid-api-key");

        if (!configured) {
            logger.debug("SendGrid no configurado. Set SENDGRID_API_KEY para habilitar envío de correos.");
        }

        return configured;
    }

    /**
     * Obtiene el email remitente configurado
     */
    public String getFromEmail() {
        return fromEmail;
    }

    /**
     * Obtiene el nombre del remitente configurado
     */
    public String getFromName() {
        return fromName;
    }
}

