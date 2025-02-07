package com.reto.inventario_inteligente.entity.password;

import com.reto.inventario_inteligente.entity.security.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String token;
    private LocalDateTime expirationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "PasswordReset{" +
                "id=" + id +
                ", user=" + user +
                ", token='" + token + '\'' +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
