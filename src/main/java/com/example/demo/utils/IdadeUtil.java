package com.example.demo.utils;

import java.time.LocalDate;
import java.time.Period;

public class IdadeUtil {

    public static Integer calculaIdade(LocalDate dataNascimento) {
        LocalDate hoje = LocalDate.now();

        return Period.between(dataNascimento, hoje).getYears();
    }
}
