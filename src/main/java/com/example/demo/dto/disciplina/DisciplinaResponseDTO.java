package com.example.demo.dto.disciplina;

import java.time.LocalDateTime;

public record DisciplinaResponseDTO(
        Long id,
        String nome,
        Integer cargaHoraria,
        String ementa,
        Boolean isAtivo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
