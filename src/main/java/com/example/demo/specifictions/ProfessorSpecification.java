package com.example.demo.specifictions;

import com.example.demo.model.Professor;
import com.example.demo.model.Usuario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfessorSpecification {

    public static Specification<Professor> nomeContem(String nome) {
        return (root, query, criteriaBuilder) -> {
            if (nome == null || nome.isBlank()) {
                return null;
            }

            Join<Professor, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(usuario.get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    public static Specification<Professor> emailContem(String email) {
        return (root, query, criteriaBuilder) ->{
            if (email == null || email.isBlank()) {
                return null;
            }

            Join<Professor, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(usuario.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Professor> cpfContem(String cpf) {
        return (root, query, criteriaBuilder) -> {
            if (cpf == null || cpf.isBlank()) {
                return null;
            }

            Join<Professor, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(usuario.get("cpf"), "%" + cpf + "%");
        };
    }

    public static Specification<Professor> isAtivo(Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            if (ativo == null) {
                return null;
            }

            Join<Professor, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.equal(usuario.get("isAtivo"), ativo);
        };
    }

    public static Specification<Professor> inicioAdmissaoDepoisDe(LocalDate inicio) {
        return (root, query, criteriaBuilder) -> {
            if (inicio == null) return null;

            LocalDateTime inicioDateTime = inicio != null ? inicio.atStartOfDay() : null;

            return criteriaBuilder.greaterThanOrEqualTo(root.get("dataAdmissao"), inicioDateTime);
        };
    }

    public static Specification<Professor> inicioAdmissaoAntesDe(LocalDate fim) {
        return (root, query, criteriaBuilder) -> {
            if (fim == null) return null;

            LocalDateTime fimDateTime = fim != null ? fim.plusDays(1).atStartOfDay() : null;

            return criteriaBuilder.lessThanOrEqualTo(root.get("dataAdmissao"), fimDateTime);
        };
    }
}