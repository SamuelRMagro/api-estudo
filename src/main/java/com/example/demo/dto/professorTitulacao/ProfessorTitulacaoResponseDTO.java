package com.example.demo.dto.professorTitulacao;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfessorTitulacaoResponseDTO(
        Long id,
        Long professorId,
        Long titulacaoId,
        String nome,
        String area,
        LocalDate dataInicio,
        LocalDate dataTermino,
        Boolean isAtivo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
