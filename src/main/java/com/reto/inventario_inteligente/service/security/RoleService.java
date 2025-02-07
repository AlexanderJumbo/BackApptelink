package com.reto.inventario_inteligente.service.security;

import com.reto.inventario_inteligente.entity.security.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findDefaultRole();
}
