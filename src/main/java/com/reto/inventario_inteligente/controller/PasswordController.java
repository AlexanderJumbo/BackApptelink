package com.reto.inventario_inteligente.controller;

import com.reto.inventario_inteligente.dto.password.UpdatePasswordRequest;
import com.reto.inventario_inteligente.service.password.PasswordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    @Autowired
    private PasswordServiceImpl passwordService;

    // Endpoint para iniciar el proceso de recuperación de contraseña
    @PostMapping("/reset-request")
    public ResponseEntity<String> resetRequest(@RequestBody String email) {
        passwordService.initiatePasswordReset(email);
        return ResponseEntity.ok("Enlace de recuperación enviado a tu correo.");
    }

    // Endpoint para validar el token y mostrar el form para resetar contraseña
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPasswordPage(@RequestParam String token) {
        boolean isValid = passwordService.validateResetToken(token);
        if (isValid) {
            //return ResponseEntity.ok("Token válido. Ahora puedes cambiar tu contraseña.");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:4200/reset-password?token=" + token)) // Cambia la URL según tu configuración
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido o expirado.");
        }
    }

    // Endpoint para actualizar la contraseña
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        passwordService.resetPassword(updatePasswordRequest.getToken(), updatePasswordRequest.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada con éxito.");
    }

}
