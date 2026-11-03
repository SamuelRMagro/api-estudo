package com.example.demo.dto.curso;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import com.example.demo.customValidations.anotacoesCustomizadas.DataPosterior;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@DataPosterior(campoInicio = "dataCadastroInicio", campoFim = "dataCadastroFim")
public record CursoFilterRequest(

        @Schema(description = "Nome do curso (aceita parcial)", defaultValue = "Java", example = "Spring")
        String nome,

        @Schema(description = "Carga horaria do curso", defaultValue = "240", example = "400")
        Integer cargaHoraria,

        @Schema(description = "Duração do curso em semestres", defaultValue = "2", example = "3")
        Integer duracaoSemestre,

        @Schema(description = "Status do curso (V/F)", defaultValue = "true", example = "false")
        Boolean isAtivo,

        @Schema(description = "Data de cadastro a partir de YYYY-MM-DD", defaultValue = "2025-01-10", example = "2025-02-05")
        @DataDentroIntervalo
        LocalDate dataCadastroInicio,

        @Schema(description = "Data de cadastro até YYYY-MM-DD", defaultValue = "2025-07-23", example = "2025-05-05")
        @DataDentroIntervalo
        LocalDate dataCadastroFim
) {
}
