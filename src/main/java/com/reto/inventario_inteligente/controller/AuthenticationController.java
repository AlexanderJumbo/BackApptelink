package com.reto.inventario_inteligente.controller;

import com.reto.inventario_inteligente.dto.auth.AuthenticationRequest;
import com.reto.inventario_inteligente.dto.auth.AuthenticationResponse;
import com.reto.inventario_inteligente.dto.auth.RegisterRequest;
import com.reto.inventario_inteligente.dto.auth.RegisterResponse;
import com.reto.inventario_inteligente.service.security.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest registerRequest){

        if(authenticationService.validateEmail(registerRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterResponse());
        }
        if(!authenticationService.validatePassword(registerRequest.getPassword())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RegisterResponse());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerUser(registerRequest));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validate(@RequestParam String jwt){
        boolean isTokenValid = authenticationService.validateToken(jwt);
        System.out.println(isTokenValid);
        return ResponseEntity.ok(isTokenValid);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        authenticationService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).body("Logout successfully");
    }
}
