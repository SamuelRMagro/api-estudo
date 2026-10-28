package com.example.demo.dto.professor;

import com.example.demo.customValidations.anotacoesCustomizadas.DataDentroIntervalo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record ProfessorCreateDTO(

        @Schema(description = "Nome do usuario")
        @NotNull(message = "O campo nome não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'nome' deve ter entre {min} e {max} caracteres.")
        String nome,

        @Schema(description = "Idade do usuario")
        @NotNull(message = "O campo idade não pode ser nulo.")
        @Min(value = 18, message = "A idade minima é de {value} anos.")
        @Max(value = 80, message = "A idade máxima é de {value} anos.")
        Integer idade,

        @Schema(description = "Login do usuario")
        @NotNull(message = "O campo login não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'login' deve ter entre {min} e {max} caracteres.")
        String login,

        @Schema(description = "Email do usuario")
        @NotNull(message = "O campo email não pode ser nulo.")
        @Email(message = "E-mail com formato inválido.")
        @Size(min = 5, max = 250, message = "O campo 'email' deve ter entre {min} e {max} caracteres.")
        String email,

        @Schema(description = "CPF do usuario")
        @NotNull(message = "O campo cpf não pode ser nulo.")
        @CPF(message = "O CPF deve ter exatamente 11 dígitos.")
        String cpf,

        @Schema(description = "Senha do usuario")
        @NotNull(message = "O campo senha não pode ser nulo.")
        @Size(min = 5, max = 50, message = "O campo 'senha' deve ter entre {min} e {max} caracteres.")
        String senha,

        @Schema(description = "Status do usuario")
        Boolean isAtivo,

        @Schema(description = "Data de admissão do professor")
        @NotNull(message = "O campo dataAdmissao não pode ser nulo.")
        @PastOrPresent(message = "A data de admissão não pode ser futura.")
        @DataDentroIntervalo
        LocalDate dataAdmissao

) {
}
