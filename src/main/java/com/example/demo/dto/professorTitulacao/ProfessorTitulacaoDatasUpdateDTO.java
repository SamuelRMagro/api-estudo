package com.example.demo.dto.professorTitulacao;

import com.example.demo.customValidations.anotacoesCustomizadas.DataPosterior;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@DataPosterior(campoInicio = "vincDataInicio", campoFim = "vincDataTermino")
public record ProfessorTitulacaoDatasUpdateDTO(

        @Schema(description = "Data de inicio da admissão do relacionamento")
        @NotNull(message = "O campo vincDataInicio não pode ser nulo.")
        @PastOrPresent(message = "A data de inicio não pode ser futura.")
        LocalDate vincDataInicio,

        @Schema(description = "Data fim da admissão do relacionamento")
        LocalDate vincDataTermino
) {
}
