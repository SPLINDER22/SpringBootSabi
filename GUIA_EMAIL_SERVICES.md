# 📧 GUÍA COMPLETA - CONFIGURACIÓN DE ENVÍO DE CORREOS

## 🎯 SOLUCIONES RECOMENDADAS

### 1. **SendGrid** ⭐⭐⭐⭐⭐ (MEJOR OPCIÓN)

#### ✅ Ventajas:
- 100 correos/día GRATIS permanentemente
- Excelente deliverability (no va a spam)
- Dashboard con estadísticas
- API REST muy simple
- Templates HTML profesionales
- Sin límites de 2FA como Gmail

#### 📝 Configuración SendGrid:

##### Paso 1: Crear cuenta
1. Ve a: https://signup.sendgrid.com/
2. Regístrate con tu email
3. Verifica tu cuenta

##### Paso 2: Obtener API Key
1. Entra al Dashboard
2. Ve a **Settings > API Keys**
3. Clic en **Create API Key**
4. Nombre: "SABI Production"
5. Permisos: **Full Access** (o Mail Send)
6. Copia el API Key (solo se muestra una vez)

##### Paso 3: Verificar dominio/email
1. Ve a **Settings > Sender Authentication**
2. Opción A: **Single Sender Verification** (más fácil)
   - Agrega: Sabi.geas5@gmail.com
   - Verifica el correo que te llegue
3. Opción B: **Domain Authentication** (profesional)
   - Si tienes dominio propio

##### Paso 4: Variables de entorno en Railway
```bash
SENDGRID_API_KEY=SG.xxxxxxxxxxxxxxxxxxxxx
SENDGRID_FROM_EMAIL=Sabi.geas5@gmail.com
SENDGRID_FROM_NAME=SABI - Salud y Bienestar
```

##### Paso 5: Agregar dependencia en pom.xml
```xml
<!-- SendGrid -->
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
    <version>4.10.2</version>
</dependency>
```

---

### 2. **Brevo (Sendinblue)** ⭐⭐⭐⭐⭐

#### ✅ Ventajas:
- 300 correos/día GRATIS
- Muy fácil de usar
- SMTP y API disponibles
- Templates visuales

#### 📝 Configuración Brevo:

##### Con SMTP (más fácil):
```properties
# En Railway
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=tu-email@ejemplo.com
MAIL_PASSWORD=tu-smtp-key-aqui
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

##### Pasos:
1. Regístrate en: https://app.brevo.com/
2. Ve a **SMTP & API**
3. Crea una **SMTP Key**
4. Usa las credenciales en Railway

---

### 3. **Mailgun** ⭐⭐⭐⭐

#### ✅ Ventajas:
- 5,000 correos/mes GRATIS (3 meses)
- API muy potente
- Para desarrolladores

#### 📝 Configuración:
1. Regístrate: https://signup.mailgun.com/
2. Verifica dominio o usa sandbox
3. Obtén API Key
4. Configura en Railway:
```bash
MAILGUN_API_KEY=key-xxxxxxxxx
MAILGUN_DOMAIN=sandboxxxxxxxxx.mailgun.org
```

---

### 4. **Gmail SMTP** ⭐⭐⭐ (Tu configuración actual)

#### ⚠️ Limitaciones:
- Solo 500 correos/día
- Requiere App Password (si tienes 2FA)
- Puede ir a spam
- Google puede bloquear

#### ✅ Ya está configurado en tu app
```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=Sabi.geas5@gmail.com
MAIL_PASSWORD=Williamespinel1  # ⚠️ Usar App Password
```

#### 📝 Para que funcione mejor:
1. Ve a: https://myaccount.google.com/security
2. Activa **2-Step Verification**
3. Ve a **App Passwords**
4. Crea una para "Mail"
5. Usa ese password en Railway

---

## 🚀 MI RECOMENDACIÓN: **SendGrid**

### ¿Por qué SendGrid?
1. ✅ **Gratis permanentemente** (100/día)
2. ✅ **No va a spam** (mejor deliverability)
3. ✅ **API simple** (3 líneas de código)
4. ✅ **Sin configuración SMTP** complicada
5. ✅ **Dashboard con estadísticas**
6. ✅ **Templates profesionales**

---

## 📦 IMPLEMENTACIÓN SENDGRID

### Archivo: `SendGridEmailService.java`

```java
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

@Service
public class SendGridEmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    @Value("${sendgrid.api.key:}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email:Sabi.geas5@gmail.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:SABI}")
    private String fromName;

    @Async
    public void sendEmail(String toEmail, String subject, String htmlContent) {
        // Si no hay API key, loggear y salir
        if (sendGridApiKey == null || sendGridApiKey.isBlank()) {
            logger.warn("SendGrid API Key no configurado. Email no enviado a: {}", toEmail);
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

    public boolean isConfigured() {
        return sendGridApiKey != null && !sendGridApiKey.isBlank();
    }
}
```

---

## 🔧 CONFIGURACIÓN EN RAILWAY

### Variables necesarias para SendGrid:
```bash
SENDGRID_API_KEY=SG.xxxxxxxxxxxxxxxxxxxxxxxxx
SENDGRID_FROM_EMAIL=Sabi.geas5@gmail.com
SENDGRID_FROM_NAME=SABI - Salud y Bienestar
```

### En application-prod.properties:
```properties
# SendGrid Configuration
sendgrid.api.key=${SENDGRID_API_KEY:}
sendgrid.from.email=${SENDGRID_FROM_EMAIL:Sabi.geas5@gmail.com}
sendgrid.from.name=${SENDGRID_FROM_NAME:SABI}
```

---

## 🎯 ESTRATEGIA HÍBRIDA (RECOMENDADO)

Usar **SendGrid como principal** y **Gmail como fallback**:

```java
@Service
public class EmailService {
    
    private final SendGridEmailService sendGridService;
    private final JavaMailSender mailSender;

    @Async
    public void enviarCorreo(String to, String subject, String content) {
        // Intentar con SendGrid primero
        if (sendGridService.isConfigured()) {
            sendGridService.sendEmail(to, subject, content);
        } else {
            // Fallback a Gmail SMTP
            enviarConJavaMailSender(to, subject, content);
        }
    }
}
```

---

## 📊 COMPARACIÓN RÁPIDA

| Servicio | Gratis/Día | Deliverability | Facilidad | Recomendado |
|----------|-----------|----------------|-----------|-------------|
| **SendGrid** | 100 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ SÍ |
| **Brevo** | 300 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ SÍ |
| **Mailgun** | 166 (5k/mes) | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⚠️ OK |
| **Gmail** | 500 | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⚠️ Temporal |

---

## ✅ PASOS SIGUIENTES

### Opción 1: Implementar SendGrid (RECOMENDADO)
1. Crear cuenta en SendGrid
2. Obtener API Key
3. Verificar email sender
4. Agregar dependencia Maven
5. Configurar variables en Railway
6. Usar el servicio

### Opción 2: Mejorar Gmail actual
1. Crear App Password de Google
2. Actualizar MAIL_PASSWORD en Railway
3. Ya funciona (limitado)

### Opción 3: Usar Brevo SMTP
1. Crear cuenta en Brevo
2. Obtener SMTP credentials
3. Actualizar variables en Railway
4. Sin cambios de código

---

## 🎉 ¿CUÁL ELIJO?

### Para PRODUCCIÓN:
🥇 **SendGrid** - Mejor deliverability y gratis permanente

### Para DESARROLLO:
🥈 **Gmail** - Ya lo tienes, funciona OK

### Para MARKETING:
🥉 **Brevo** - 300/día y templates visuales

---

¿Quieres que implemente SendGrid ahora? Solo dime y lo configuro completamente en tu proyecto.

