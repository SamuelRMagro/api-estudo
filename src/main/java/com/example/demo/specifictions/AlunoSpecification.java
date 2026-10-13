package com.example.demo.specifictions;

import com.example.demo.model.Aluno;
import com.example.demo.model.Usuario;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class AlunoSpecification {

    public static Specification<Aluno> nomeContem(String nome) {
        return (root, query, criteriaBuilder) -> {
            if (nome == null || nome.isBlank()) {
                return null;
            }

            Join<Aluno, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(usuario.get("nome")), "%" + nome.toLowerCase() + "%");
        };
    }

    public static Specification<Aluno> emailContem(String email) {
        return (root, query, criteriaBuilder) ->{
            if (email == null || email.isBlank()) {
                return null;
            }

            Join<Aluno, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(criteriaBuilder.lower(usuario.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Aluno> cpfContem(String cpf) {
        return (root, query, criteriaBuilder) -> {
            if (cpf == null || cpf.isBlank()) {
                return null;
            }

            Join<Aluno, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.like(usuario.get("cpf"), "%" + cpf + "%");
        };
    }

    public static Specification<Aluno> isAtivo(Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            if (ativo == null) {
                return null;
            }

            Join<Aluno, Usuario> usuario = root.join("usuario", JoinType.LEFT);
            return criteriaBuilder.equal(usuario.get("isAtivo"), ativo);
        };
    }
}
