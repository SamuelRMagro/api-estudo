package com.example.demo.specifictions;

import com.example.demo.model.Disciplina;
import org.springframework.data.jpa.domain.Specification;

public class DisciplinaSpecification {

    public static Specification<Disciplina> nomeContem(String nome) {
        return (root, query, criteriaBuilder) ->
                nome == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    public static Specification<Disciplina> ementaContem(String ementa) {
        return (root, query, criteriaBuilder) ->
                ementa == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("ementa")), "%" + ementa.toLowerCase() + "%");
    }

    public static Specification<Disciplina> isAtivo(Boolean ativo) {
        return (root, query, criteriaBuilder) ->
                ativo == null ? null : criteriaBuilder.equal(root.get("isAtivo"), ativo);
    }

    public static Specification<Disciplina> cargaHorariaAte(Integer cargaHoraria) {
        return (root, query, criteriaBuilder) -> {
            if (cargaHoraria == null) return null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("cargaHoraria"), cargaHoraria);
        };
    }
}
