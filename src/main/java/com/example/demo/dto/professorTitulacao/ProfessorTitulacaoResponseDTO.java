package com.example.demo.dto.professorTitulacao;

import java.time.LocalDate;

public record ProfessorTitulacaoResponseDTO(
        Long id,
        LocalDate dataInicio,
        LocalDate dataTermino,
        Long professorId,
        Long titulacaoId,
        LocalDate dataCriacao,
        Boolean isAtivo
) {
}
