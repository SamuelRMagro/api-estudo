package com.example.demo.dto.aluno;

import com.example.demo.customValidations.anotacoesCustomizadas.DataNascimento;
import com.example.demo.customValidations.anotacoesCustomizadas.Telefone;
import com.example.demo.dto.usuario.UsuarioCreateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AlunoCreateDTO(
        UsuarioCreateDTO usuario,

        @Schema(description = "Telefone do aluno")
        @Telefone
        String telefone,

        @Schema(description = "Data de nascimento do aluno")
        @NotNull(message = "O campo telefone não pode ser nulo")
        @DataNascimento
        LocalDate dataNascimento
) {
}
