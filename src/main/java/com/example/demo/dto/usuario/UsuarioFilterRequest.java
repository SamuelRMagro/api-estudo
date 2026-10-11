package com.example.demo.dto.usuario;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record UsuarioFilterRequest(

        @Schema(description = "Nome do aluno (aceita parcial)", defaultValue = "Ana", example = "João")
        String nome,

        @Schema(description = "Email do aluno (aceita parcial)", defaultValue = "ana.souza@email.com", example = "joao.pereira@email.com")
        String email,

        @Schema(description = "Status do aluno (V/F)", defaultValue = "true", example = "false")
        Boolean isAtivo,

        @Schema(description = "Data de cadastro a partir de YYYY-MM-DD", defaultValue = "2026-03-02", example = "2026-05-14")
        @DataDentroIntervalo
        LocalDate dataCadastroInicio,

        @Schema(description = "Data de cadastro até YYYY-MM-DD", defaultValue = "2026-08-05", example = "2022-06-01")
        @DataDentroIntervalo
        LocalDate dataCadastroFim
) {
}
