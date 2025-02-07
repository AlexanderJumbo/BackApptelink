package com.reto.inventario_inteligente.service.password;

public interface PasswordService {
    void initiatePasswordReset(String email);
    boolean validateResetToken(String token);
    void resetPassword(String token, String newPassword);
}
