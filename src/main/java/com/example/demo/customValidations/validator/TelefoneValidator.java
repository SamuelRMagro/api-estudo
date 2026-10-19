package com.example.demo.customValidations.validator;

import com.example.demo.customValidations.anotacoesCustomizadas.Telefone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator  implements ConstraintValidator<Telefone, String> {
    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {

        if (valor == null || valor.isBlank()) {
            return true;
        }

        String numeros = valor.replaceAll("\\D", "");

        if (numeros.length() == 10) {
            return numeros.matches("^[1-9]{2}[2-5]\\d{7}$");
        }
        if (numeros.length() == 11) {
            return numeros.matches("^[1-9]{2}9\\d{8}$");
        }
        return false;
    }
}
