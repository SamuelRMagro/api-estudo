package com.example.demo.customValidations.validator;

import com.example.demo.customValidations.anotacoesCustomizadas.IntervaloNumerico;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;

public class IntervaloNumericoValidator implements ConstraintValidator<IntervaloNumerico, Object> {

    private String campoMinimo;
    private String campoMaximo;

    @Override
    public void initialize(IntervaloNumerico anotacao) {
        this.campoMinimo = anotacao.campoMinimo();
        this.campoMaximo = anotacao.campoMaximo();
    }

    @Override
    public boolean isValid(Object objeto, ConstraintValidatorContext context) {
        if (objeto == null) {
            return true;
        }

        Integer minimo = lerInteiro(objeto, campoMinimo);
        Integer maximo = lerInteiro(objeto, campoMaximo);

        if (minimo == null || maximo == null || minimo <= maximo) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(campoMaximo)
                .addConstraintViolation();
        return false;
    }

    private Integer lerInteiro(Object objeto, String campo) {
        try {
            Method acessor = objeto.getClass().getMethod(campo);
            Object valor = acessor.invoke(objeto);
            if (valor == null) {
                return null;
            }
            if (valor instanceof Integer inteiro) {
                return inteiro;
            }
            throw new IllegalStateException(
                    "O campo '" + campo + "' em " + objeto.getClass().getSimpleName() + " deve ser Integer.");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Não foi possível ler o campo '" + campo + "' em " + objeto.getClass().getSimpleName(), e);
        }
    }
}
