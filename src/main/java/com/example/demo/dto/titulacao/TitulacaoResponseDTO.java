package com.example.demo.dto.titulacao;

public record TitulacaoResponseDTO(
        Long id,
        String area,
        String nome,
        Boolean isAtivo
) {
}
