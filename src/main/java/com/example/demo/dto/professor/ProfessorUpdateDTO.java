package com.example.demo.dto.professor;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProfessorUpdateDTO(

        @Valid
        UsuarioUpdateDTO usuario,

        @Schema(description = "Login do professor")
        @NotNull(message = "O campo login não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'login' deve ter entre {min} e {max} caracteres.")
        String login,

        @Schema(description = "Data de admissão do professor")
        @NotNull(message = "O campo dataAdmissao não pode ser nulo.")
        @PastOrPresent(message = "A data de admissão não pode ser futura.")
        @DataDentroIntervalo
        LocalDate dataAdmissao
        ) {
}
