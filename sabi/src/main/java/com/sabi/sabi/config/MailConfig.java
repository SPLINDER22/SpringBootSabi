package com.sabi.sabi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private String starttlsStr;

    private boolean getStarttls() {
        if (starttlsStr == null || starttlsStr.isBlank()) {
            return true; // default to true if not set
        }
        return Boolean.parseBoolean(starttlsStr);
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        // don't set username/password if empty - allow app to start without mail configured
        if (username != null && !username.isBlank()) {
            mailSender.setUsername(username);
        }
        if (password != null && !password.isBlank()) {
            mailSender.setPassword(password);
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", (username != null && !username.isBlank()) ? "true" : "false");
        props.put("mail.smtp.starttls.enable", String.valueOf(getStarttls()));
        // timeouts to fail fast in environments where SMTP is blocked
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        // trust github's smtp host if using gmail
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.debug", "false");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            System.out.println("⚠️ Mail sender not fully configured - MAIL_USERNAME or MAIL_PASSWORD missing. Email sending will be disabled until configured.");
        } else {
            System.out.println("✅ Mail sender configured for host: " + host + " port: " + port + " user: " + username);
        }

        return mailSender;
    }
}
