package com.reto.inventario_inteligente.service.security;

import com.reto.inventario_inteligente.dto.auth.AuthenticationRequest;
import com.reto.inventario_inteligente.dto.auth.AuthenticationResponse;
import com.reto.inventario_inteligente.dto.auth.RegisterRequest;
import com.reto.inventario_inteligente.dto.auth.RegisterResponse;
import com.reto.inventario_inteligente.entity.security.JwtToken;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.repository.security.JwtTokenRepository;
import com.reto.inventario_inteligente.repository.security.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;
    @Autowired
    private UserRepository userRepository;

    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        User user = userService.registerUser(registerRequest);
        //String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        //saveUserToken(user, jwt);

        return mapRegisterResponse(user/*, jwt*/);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        System.out.println("credenciales: " + authenticationRequest.getUsername() + " " + authenticationRequest.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()
        );

        authenticationManager.authenticate(authentication);

        User user = userService.findByUsername(authenticationRequest.getUsername()).get();
        System.out.println("user " + user);

        String jwt = jwtService.generateToken(user, generateExtraClaims(user));

        saveUserToken(user, jwt);

        return mapDTO(jwt, user);
    }
    private Map<String, Object> generateExtraClaims(User user) {

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getFirstname());
        extraClaims.put("role", user.getRole().getName());
        extraClaims.put("authorities", user.getAuthorities());

        return extraClaims;
    }

    private void saveUserToken(User user, String jwt) {

        JwtToken token = new JwtToken();
        token.setToken(jwt);
        token.setUser(user);
        token.setExpiration(jwtService.extractExpiration(jwt));
        token.setValid(true);

        jwtTokenRepository.save(token);
    }

    public boolean validateToken(String jwt) {
        try {
            jwtService.extractUsername(jwt);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void logout(HttpServletRequest request) {
        String jwt = jwtService.extractJwtFromRequest(request);

        if(jwt == null || !StringUtils.hasText(jwt)) return;

        Optional<JwtToken> token = jwtTokenRepository.findByToken(jwt);
        if(token.isPresent() && token.get().isValid()){
            token.get().setValid(false);
            jwtTokenRepository.save(token.get());
        }
    }

    public Boolean validateEmail(String email){
        return userRepository.findByEmail(email).isPresent();
    }

    public Boolean validatePassword(String password){
        String passworPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        if(!password.matches(passworPattern)){
            return false;
        }
        return true;
    }

    private AuthenticationResponse mapDTO(String jwt, User user) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUserId(user.getId());

        return authenticationResponse;
    }

    private static RegisterResponse mapRegisterResponse(User user) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setName(user.getFirstname().concat(user.getLastname()));
        registerResponse.setUserId(user.getId());
        registerResponse.setUsername(user.getUsername());
        registerResponse.setEmail(user.getEmail());
        registerResponse.setRole(user.getRole().getName());
        return registerResponse;
    }
}
