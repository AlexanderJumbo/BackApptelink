package com.reto.inventario_inteligente.dto;

import java.io.Serializable;

public class UserResponse implements Serializable {

    private String name;
    private Long id;
    private String dni;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
