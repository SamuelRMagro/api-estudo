package com.example.demo.exceptions.custom;

public class VinculoJaExistente extends RuntimeException {
    public VinculoJaExistente() {
        super("Vínculo já existente");
    }
}
