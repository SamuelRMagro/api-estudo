package com.example.demo.exceptions.custom;

public class DadosInconsistentesCustomException extends RuntimeException {
    public DadosInconsistentesCustomException(String message) {
        super(message);
    }
}
