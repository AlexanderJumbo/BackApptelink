package com.reto.inventario_inteligente.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendPasswordResetEmail(String email, String token) {
        String subject = "Recuperación de Contraseña";
        String resetLink = "http://localhost:9191/api/password/reset-password?token=" + token;

        String body = "Haga clic en el siguiente enlace para restablecer su contraseña: " + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("geossephyjumboalava@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }
}
