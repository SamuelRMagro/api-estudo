package com.example.demo.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioUpdateDTO(

        @Schema(description = "Nome do usuario", defaultValue = "Ana Souza")
        @NotBlank(message = "O campo nome não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'nome' deve ter entre {min} e {max} caracteres.")
        String nome,

        @Schema(description = "Idade do usuario")
        @NotNull(message = "O campo idade não pode ser nulo.")
        @Min(value = 15, message = "A idade minima é de {value} anos.")
        @Max(value = 130, message = "A idade máxima é de {value} anos.")
        Integer idade,

        @Schema(description = "Email do usuario")
        @NotBlank(message = "O campo email não pode ser nulo.")
        @Email(message = "E-mail com formato inválido.")
        @Size(min = 5, max = 250, message = "O campo 'email' deve ter entre {min} e {max} caracteres.")
        String email,

        @Schema(description = "CPF do usuario")
        @NotBlank(message = "O CPF deve ter exatamente 11 dígitos.")
        @CPF(message = "Informe um CPF válido.")
        String cpf
) {
}
