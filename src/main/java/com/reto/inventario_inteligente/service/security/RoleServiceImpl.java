package com.reto.inventario_inteligente.service.security;

import com.reto.inventario_inteligente.entity.security.Role;
import com.reto.inventario_inteligente.repository.security.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Optional<Role> findDefaultRole() {
        return roleRepository.findByName("USER");
    }
}
