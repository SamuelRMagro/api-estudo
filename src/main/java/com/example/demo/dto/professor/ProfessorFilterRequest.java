package com.example.demo.dto.professor;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ProfessorFilterRequest(

        @Schema(description = "Nome do professor (aceita parcial)", defaultValue = "Ana", example = "Carla")
        String nome,

        @Schema(description = "Email do professor (aceita parcial)", defaultValue = "ana.souza@email.com", example = "carla.mendes@email.com")
        String email,

        @Schema(description = "cpf do professor (aceita parcial)", defaultValue = "906", example = "034")
        String cpf,

        @Schema(description = "Status do professor (V/F)", defaultValue = "true", example = "false")
        Boolean isAtivo,

        @Schema(description = "Data de admissao a partir de YYYY-MM-DD", defaultValue = "2018-02-05", example = "2019-08-12")
        @DataDentroIntervalo
        LocalDate dataAdmissaoInicio,

        @Schema(description = "Data de admissao até YYYY-MM-DD", defaultValue = "2023-08-07", example = "2021-02-15")
        @DataDentroIntervalo
        LocalDate dataAdmissaoFim
) {
}