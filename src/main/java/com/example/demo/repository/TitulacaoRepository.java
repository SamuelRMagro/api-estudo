package com.example.demo.repository;

import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.model.Titulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TitulacaoRepository extends JpaRepository<Titulacao, Long> {

    @Query(value = """
        SELECT t.area, t.nome
        FROM tb_titulacoes t
        join tb_professor_titulacoes pt on t.id = pt.titulacao_id
        where pt.professor_id = :professorId
""", nativeQuery = true)
    List<TitulacaoResponseDTO> buscaTitulacoesProfessorId(@Param("professorId") Long professorId);
}
