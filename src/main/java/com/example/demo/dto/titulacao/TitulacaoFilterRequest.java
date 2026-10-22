package com.example.demo.dto.titulacao;

import io.swagger.v3.oas.annotations.media.Schema;

public record TitulacaoFilterRequest(

        @Schema(description = "Area da titulação (aceita parcial)")
        String area,

        @Schema(description = "Nome da titulação (aceita parcial)")
        String nome
) {
}
