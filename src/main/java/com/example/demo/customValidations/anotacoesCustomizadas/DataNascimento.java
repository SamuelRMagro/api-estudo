package com.example.demo.customValidations.anotacoesCustomizadas;

import com.example.demo.customValidations.validator.DataNascimentoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataNascimentoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataNascimento {

    String message() default "A idade deve estar entre {idadeMinima} e {idadeMaxima} anos.";

    int idadeMinima() default 17;

    int idadeMaxima() default 80;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
