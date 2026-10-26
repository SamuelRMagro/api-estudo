package com.example.demo.dto.professor;

import com.example.demo.dto.professorTitulacao.ProfessorIdTitulacoesResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record ProfessorIdResponseDTO(
        Long profId,
        Long userId,
        String userNome,
        String userEmail,
        String userCpf,
        Boolean userAtivo,
        LocalDate userDataCriacao,
        LocalDate userDataAtualizacao,
        LocalDate profDataAdmissao,
        List<ProfessorIdTitulacoesResponseDTO> titulacoes
) {
}
