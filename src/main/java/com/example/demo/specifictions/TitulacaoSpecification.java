package com.example.demo.specifictions;

import com.example.demo.model.Titulacao;
import org.springframework.data.jpa.domain.Specification;

public class TitulacaoSpecification {

    public static Specification<Titulacao> nomeContem(String nome) {
        return (root, query, criteriaBuilder) ->
                nome == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Titulacao> areaContem(String area) {
        return (root, query, criteriaBuilder) ->
                area == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("area")), "%" + area.toLowerCase() + "%");
    }
}
