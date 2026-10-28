package com.example.demo.dto.professor;

import com.example.demo.dto.usuario.UsuarioResponseDTO;

import java.time.LocalDate;

public record ProfessorResponseDTO(
        Integer id,
        UsuarioResponseDTO usuario,
        LocalDate dataAdmissao
) {
}
