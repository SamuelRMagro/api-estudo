package com.example.demo.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioCreateDTO(

        @Schema(description = "Nome do usuario")
        @NotBlank(message = "O campo nome não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'nome' deve ter entre {min} e {max} caracteres.")
        String nome,

        @Schema(description = "Idade do usuario")
        @NotNull(message = "O campo idade não pode ser nulo.")
        @Min(value = 15, message = "A idade minima é de {value} anos.")
        @Max(value = 130, message = "A idade máxima é de {value} anos.")
        Integer idade,

        @Schema(description = "Login do usuario")
        @NotBlank(message = "O campo login não pode ser nulo.")
        @Size(min = 5, max = 250, message = "O campo 'login' deve ter entre {min} e {max} caracteres.")
        String login,

        @Schema(description = "Email do usuario")
        @NotBlank(message = "O campo email não pode ser nulo.")
        @Email(message = "E-mail com formato inválido.")
        @Size(min = 5, max = 250, message = "O campo 'email' deve ter entre {min} e {max} caracteres.")
        String email,

        @Schema(description = "CPF do usuario")
        @NotBlank(message = "O campo cpf não pode ser nulo.")
        @CPF(message = "O CPF deve ter exatamente 11 dígitos.")
        String cpf,

        @Schema(description = "Senha do usuario")
        @NotBlank(message = "O campo senha não pode ser nulo.")
        @Size(min = 5, max = 50, message = "O campo 'senha' deve ter entre {min} e {max} caracteres.")
        String senha,

        @Schema(description = "Status do usuario")
        Boolean isAtivo) {
}
