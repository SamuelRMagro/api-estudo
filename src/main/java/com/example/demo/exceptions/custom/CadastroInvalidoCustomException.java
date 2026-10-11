package com.example.demo.exceptions.custom;

import com.example.demo.dto.exception.ErroCampo;

import java.util.List;

public class CadastroInvalidoCustomException extends RuntimeException {
    private List<ErroCampo> erros;

    public CadastroInvalidoCustomException(List<ErroCampo> erros) {
        super("Existem campos já cadastrados");
        this.erros = erros;
    }

    public List<ErroCampo> getErros() {
        return erros;
    }
}
