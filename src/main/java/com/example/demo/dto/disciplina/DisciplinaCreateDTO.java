package com.example.demo.dto.disciplina;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DisciplinaCreateDTO(

        @Schema(description = "Nome da disciplina")
        @NotNull(message = "O campo nome não pode ser nulo.")
        @Size(min = 5, max = 255, message = "O campo nome deve ter entre {min} e {max} caracteres.")
        String nome,

        @Schema(description = "Carga horaria da disciplina")
        @NotNull(message = "O campo cargaHoraria não pode ser nulo.")
        @Min(value = 10, message = "A carga horaria minima é de {value} horas.")
        @Max(value = 10000, message = "A carga horaria máxima é de {value} horas.")
        Integer cargaHoraria,

        @Schema(description = "Ementa da disciplina")
        @NotNull(message = "O campo ementa não pode ser nulo.")
        @Size(min = 20, max = 5000, message = "O campo ementa deve ter entre {min} e {max} caracteres.")
        String ementa
) {
}
