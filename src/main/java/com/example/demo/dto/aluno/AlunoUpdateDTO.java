package com.example.demo.dto.aluno;

import com.example.demo.customValidations.anotacoesCustomizadas.DataNascimento;
import com.example.demo.customValidations.anotacoesCustomizadas.Telefone;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlunoUpdateDTO(

        @Valid
        UsuarioUpdateDTO usuario,

        @Schema(description = "Login do aluno")
        @NotNull(message = "O campo login não pode ser nulo.")
        @Size(min = 3, max = 50, message = "O campo 'login' deve ter entre {min} e {max} caracteres.")
        String login,

        @Schema(description = "Data de nascimento do aluno")
        @NotNull(message = "O campo telefone não pode ser nulo")
        @DataNascimento
        LocalDate dataNascimento,

        @Schema(description = "Telefone do aluno")
        @Telefone
        String telefone
) {
}
