package com.example.demo.customValidations.anotacoesCustomizadas;

import com.example.demo.customValidations.validator.IntervaloNumericoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IntervaloNumericoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(IntervaloNumerico.List.class)
public @interface IntervaloNumerico {

    String message() default "O valor mínimo não pode ser maior que o valor máximo.";

    String campoMinimo();

    String campoMaximo();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        IntervaloNumerico[] value();
    }
}
