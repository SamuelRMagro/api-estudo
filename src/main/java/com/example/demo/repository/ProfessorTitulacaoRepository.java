package com.example.demo.repository;

import com.example.demo.model.ProfessorTitulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;

@Repository
public interface ProfessorTitulacaoRepository extends JpaRepository<ProfessorTitulacao, Long> {

    @Query(value = """
        SELECT COUNT(*)
        FROM tb_titulacoes t
        join tb_professor_titulacoes pt on t.id = pt.titulacao_id
        where pt.professor_id = :professorId and pt.data_inicio = :dataInicio and pt.vinculo = true
    """, nativeQuery = true)
    BigInteger quantVinculos(@Param("dataInicio") LocalDate dataInicio, @Param("professorId") Long professorId);
}
