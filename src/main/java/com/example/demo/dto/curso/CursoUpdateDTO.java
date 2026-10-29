package com.example.demo.dto.curso;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CursoUpdateDTO(

        @Schema(description = "Nome do curso", defaultValue = "Java Básico")
        @NotBlank(message = "O campo nome não pode ser nulo.")
        @Size(min = 5, max = 255, message = "O campo nome deve ter entre {min} e {max} caracteres.")
        String nome,

        @Schema(description = "Carga horaria do curso")
        @NotNull(message = "O campo cargaHoraria não pode ser nulo.")
        @Min(value = 10, message = "A carga horaria minima é de {value} horas.")
        @Max(value = 10000, message = "A carga horaria máxima é de {value} horas.")
        Integer cargaHoraria,

        @Schema(description = "Quantidade de semestres do curso")
        @NotNull(message = "O campo duracaoSemestre não pode ser nulo.")
        @Min(value = 5, message = "A duracao em semestre minima é de {value} semestres.")
        @Max(value = 10, message = "A duracao em semestre máxima é de {value} semestres.")
        Integer duracaoSemestre
) {
}
