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
        return valor.matches("[0-9]{10,11}");
    }
}
