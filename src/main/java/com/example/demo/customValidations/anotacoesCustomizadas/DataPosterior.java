package com.example.demo.customValidations.anotacoesCustomizadas;

import com.example.demo.customValidations.validator.DataPosteriorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DataPosteriorValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPosterior {

    String message() default "A data final não pode ser anterior à data inicial.";

    String campoInicio();

    String campoFim();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
