package com.example.demo.dto.disciplina;

import io.swagger.v3.oas.annotations.media.Schema;

public record DisciplinaFilterRequest(

        @Schema(description = "Nome da disciplina (aceita parcial)", defaultValue = "", example = "")
        String nome,

        @Schema(description = "Ementa da disciplina (aceita parcial)", defaultValue = "", example = "")
        String ementa,

        @Schema(description = "Status da disciplina (V/F)", defaultValue = "true", example = "false")
        Boolean isAtivo,

        @Schema(description = "Carga horaria da disciplina", defaultValue = "", example = "")
        Integer cargaHoraria
) {
}
