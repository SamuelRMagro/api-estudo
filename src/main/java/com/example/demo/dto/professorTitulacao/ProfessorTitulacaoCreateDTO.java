package com.example.demo.dto.professorTitulacao;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import com.example.demo.customValidations.anotacoesCustomizadas.DataPosterior;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

@DataPosterior(campoInicio = "dataInicio", campoFim = "dataTermino")
public record ProfessorTitulacaoCreateDTO(

        @Schema(description = "Id da titulacao")
        @NotNull(message = "O campo titulacaoId não pode ser nulo.")
        Long titulacaoId,

        @Schema(description = "Data de inicio da admissão do relacionamento")
        @NotNull(message = "O campo dataInicio não pode ser nulo.")
        @PastOrPresent(message = "A data de inicio não pode ser futura.")
        @DataDentroIntervalo
        LocalDate dataInicio,

        @Schema(description = "Data fim da admissão do relacionamento")
        LocalDate dataTermino
) {
}
