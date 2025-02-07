package com.reto.inventario_inteligente.service.password;

import com.reto.inventario_inteligente.entity.password.PasswordReset;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.repository.password.PasswordRepository;
import com.reto.inventario_inteligente.repository.security.UserRepository;
import com.reto.inventario_inteligente.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordServiceImpl implements PasswordService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
    @Override
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email).get();

        if (user != null) {
            String token = UUID.randomUUID().toString();  // Genera un token único
            PasswordReset resetToken = new PasswordReset();
            resetToken.setUser(user);
            resetToken.setToken(token);
            resetToken.setExpirationDate(LocalDateTime.now().plusHours(1)); // Token válido por 1 hora
            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(user.getEmail(), token);
        }
    }
    @Override
    public boolean validateResetToken(String token) {
        System.out.println("Token"+token);
        PasswordReset resetToken = tokenRepository.findByToken(token).get();
        if (resetToken != null && resetToken.getExpirationDate().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }
    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordReset resetToken = tokenRepository.findByToken(token).get();
        System.out.println("RESET TOKEN: " + resetToken);
        if (resetToken != null && resetToken.getExpirationDate().isAfter(LocalDateTime.now())) {
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
        }
    }
}
