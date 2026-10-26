package com.example.demo.dto.professorTitulacao;

import java.time.LocalDate;

public record ProfessorIdTitulacoesResponseDTO(
        Long vincId,
        Long tituId,
        String tituNome,
        String tituArea,
        LocalDate vincDataInicio,
        LocalDate vincDataTermino,
        Boolean vincAtivo
) {
}
