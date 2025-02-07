package com.reto.inventario_inteligente.repository.security;

import com.reto.inventario_inteligente.dto.UserResponse;
import com.reto.inventario_inteligente.entity.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.status = true")
    Optional<User> findByUsername(String user);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.role.id = 2")
    List<User> getUsers();
    @Modifying
    @Query("UPDATE User u SET u.status = false WHERE u.username = ?1")
    void blockAccount(String user);

}
