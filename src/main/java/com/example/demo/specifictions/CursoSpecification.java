package com.example.demo.specifictions;

import com.example.demo.model.Curso;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CursoSpecification {

    public static Specification<Curso> nomeContem(String nome) {
        return (root, query, criteriaBuilder) ->
                nome == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Curso> cargaHorariaAte(Integer cargaHoraria) {
        return (root, query, criteriaBuilder) -> {
            if (cargaHoraria == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("cargaHoraria"), cargaHoraria);
        };
    }

    public static Specification<Curso> duracaoSemestreAte(Integer duracaoSemestre) {
        return (root, query, criteriaBuilder) -> {
            if(duracaoSemestre == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("duracaoSemestre"), duracaoSemestre);
        };
    }

    public static Specification<Curso> isAtivo(Boolean ativo) {
        return (root, query, criteriaBuilder) ->
                ativo == null ? null : criteriaBuilder.equal(root.get("isAtivo"), ativo);
    }

    public static Specification<Curso> inicioDepoisDe(LocalDate inicio) {
        return (root, query, criteriaBuilder) -> {
            if (inicio == null) return null;

            LocalDateTime inicioDateTime = inicio != null ? inicio.atStartOfDay() : null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("dataCriacao"), inicioDateTime);
        };
    }

    public static Specification<Curso> inicioAntesDe(LocalDate fim) {
        return (root, query, criteriaBuilder) -> {
            if (fim == null) return null;

            LocalDateTime fimDateTime = fim != null ? fim.plusDays(1).atStartOfDay() : null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("dataCriacao"), fimDateTime);
        };
    }
}
