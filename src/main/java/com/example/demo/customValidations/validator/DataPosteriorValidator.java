package com.example.demo.customValidations.validator;

import com.example.demo.customValidations.anotacoesCustomizadas.DataPosterior;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.time.LocalDate;

public class DataPosteriorValidator implements ConstraintValidator<DataPosterior, Object> {


    private String campoInicio;
    private String campoFim;

    @Override
    public void initialize(DataPosterior anotacao) {
        this.campoInicio = anotacao.campoInicio();
        this.campoFim = anotacao.campoFim();
    }

    @Override
    public boolean isValid(Object objeto, ConstraintValidatorContext context) {
        if (objeto == null) {
            return true;
        }

        LocalDate inicio = lerData(objeto, campoInicio);
        LocalDate fim = lerData(objeto, campoFim);

        if (inicio == null || fim == null) {
            return true;
        }

        boolean valido = !fim.isBefore(inicio);

        if (!valido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(campoFim)
                    .addConstraintViolation();
        }

        return valido;
    }

    private LocalDate lerData(Object objeto, String campo) {
        try {
            Method acessor = objeto.getClass().getMethod(campo);
            return (LocalDate) acessor.invoke(objeto);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Não foi possível ler o campo '" + campo + "' em " + objeto.getClass().getSimpleName(), e);
        }
    }
}
