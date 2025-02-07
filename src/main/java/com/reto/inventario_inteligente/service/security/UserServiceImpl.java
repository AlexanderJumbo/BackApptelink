package com.reto.inventario_inteligente.service.security;

import com.reto.inventario_inteligente.dto.UserResponse;
import com.reto.inventario_inteligente.dto.auth.RegisterRequest;
import com.reto.inventario_inteligente.entity.security.Role;
import com.reto.inventario_inteligente.entity.security.User;
import com.reto.inventario_inteligente.exception.ObjectNotFoundException;
import com.reto.inventario_inteligente.repository.security.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        User newUser = new User();
        newUser.setFirstname(registerRequest.getFirstname());
        newUser.setLastname(registerRequest.getLastname());
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setDni(registerRequest.getDni());
        newUser.setAddress(registerRequest.getAddress());
        newUser.setNumberPhone(registerRequest.getNumberPhone());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setStatus(true);

        Role role = roleService.findDefaultRole()
                .orElseThrow(() -> new ObjectNotFoundException("Role not found."));
        newUser.setRole(role);

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){

        }
        return userRepository.save(newUser);
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.getUsers().stream().map(client -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(client.getId());
            userResponse.setName(client.getFirstname() + " " + client.getLastname());
            userResponse.setDni(client.getDni());
            return userResponse;
        }).collect(Collectors.toList());
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void blockAccount(String username) {
        Query query = entityManager.createQuery("UPDATE User u SET u.status = false WHERE u.username = :username");
        query.setParameter("username", username);
        int result = query.executeUpdate();
        // Lógica si es necesario
    }
}
