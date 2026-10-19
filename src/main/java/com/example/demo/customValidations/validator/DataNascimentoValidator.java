package com.example.demo.customValidations.validator;

import com.example.demo.customValidations.anotacoesCustomizadas.DataNascimento;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class DataNascimentoValidator implements ConstraintValidator<DataNascimento, LocalDate> {

    private int idadeMinima;
    private int idadeMaxima;

    @Override
    public void initialize(DataNascimento constraintAnnotation) {
        this.idadeMinima = constraintAnnotation.idadeMinima();
        this.idadeMaxima = constraintAnnotation.idadeMaxima();
    }

    @Override
    public boolean isValid(LocalDate dataNascimento, ConstraintValidatorContext context) {
        if (dataNascimento == null) {
            return true;
        }

        LocalDate hoje = LocalDate.now();

        if (dataNascimento.isAfter(hoje)) {
            return false;
        }

        int idade = Period.between(dataNascimento, hoje).getYears();
        return idade >= idadeMinima && idade <= idadeMaxima;
    }
}

