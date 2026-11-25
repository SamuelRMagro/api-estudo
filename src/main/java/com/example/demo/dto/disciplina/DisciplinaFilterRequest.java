package com.example.demo.dto.disciplina;

import com.example.demo.customValidations.anotacoesCustomizadas.IntervaloNumerico;
import io.swagger.v3.oas.annotations.media.Schema;

@IntervaloNumerico(campoMinimo = "cargaHorariaMin", campoMaximo = "cargaHorariaMax")
public record DisciplinaFilterRequest(

        @Schema(description = "Nome da disciplina (aceita parcial)", defaultValue = "", example = "")
        String nome,

        @Schema(description = "Ementa da disciplina (aceita parcial)", defaultValue = "", example = "")
        String ementa,

        @Schema(description = "Status da disciplina (V/F)", defaultValue = "true", example = "false")
        Boolean isAtivo,

        @Schema(description = "Carga horaria mínima da disciplina", defaultValue = "", example = "")
        Integer cargaHorariaMin,

        @Schema(description = "Carga horaria máxima da disciplina", defaultValue = "", example = "")
        Integer cargaHorariaMax
) {
}
