package com.sabi.sabi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String subject, String message, List<String> recipients) {
        String footer = "\n\n---\nEste correo fue enviado por medio de la app de Sabi.";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setSubject(subject);
        mailMessage.setText(message + footer);
        mailMessage.setTo(recipients.toArray(new String[0]));
        mailSender.send(mailMessage);
    }
}
