package com.example.demo.dto.titulacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TitulacaoCreateDTO(

        @Schema(description = "Nome da titulacao")
        @NotNull(message = "O campo nome não deve ser nulo")
        @Size(min = 5, max = 255, message = "O campo nome deve ter entre {min} e {max} caracteres")
        String nome,

        @Schema(description = "Nome da titulacao")
        @NotNull(message = "O campo area não deve ser nulo")
        @Size(min = 3, max = 100, message = "O campo area deve ter entre {min} e {max} caracteres")
        String area
) {
}
