package com.reto.inventario_inteligente.dto.auth;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {
    private String jwt;
    private Long userId;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
