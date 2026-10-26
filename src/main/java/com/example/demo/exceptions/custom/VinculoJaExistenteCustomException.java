package com.example.demo.exceptions.custom;

public class VinculoJaExistenteCustomException extends RuntimeException {
    public VinculoJaExistenteCustomException() {
        super("Vínculo já existente");
    }
}
