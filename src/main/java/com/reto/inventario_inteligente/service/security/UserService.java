package com.reto.inventario_inteligente.service.security;

import com.reto.inventario_inteligente.dto.UserResponse;
import com.reto.inventario_inteligente.dto.auth.RegisterRequest;
import com.reto.inventario_inteligente.entity.security.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User registerUser(RegisterRequest registerRequest);

    List<UserResponse> getUsers();
}
