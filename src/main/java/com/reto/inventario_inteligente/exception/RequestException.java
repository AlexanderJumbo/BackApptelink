package com.reto.inventario_inteligente.exception;

public class RequestException extends RuntimeException{

    private int code;
    public RequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
