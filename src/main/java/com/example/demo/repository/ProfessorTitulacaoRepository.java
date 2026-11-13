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
        FROM tb_professor_titulacoes pt
        WHERE pt.professor_id = :professorId AND pt.data_inicio = :dataInicio AND pt.vinculo = true AND pt.titulacao_id = :titulacaoId
    """, nativeQuery = true)
    BigInteger quantVinculos(@Param("dataInicio") LocalDate dataInicio, @Param("professorId") Long professorId,
                             @Param("titulacaoId") Long titulacaoId);

    @Query(value = """
        SELECT COUNT(*)
        FROM tb_professor_titulacoes pt
        WHERE pt.professor_id = :professorId AND pt.data_inicio = :dataInicio
        AND pt.vinculo = true AND pt.titulacao_id = :titulacaoId AND pt.id <> :idDoVinculoEditado
    """, nativeQuery = true)
    BigInteger verificarOutrosVinculosParaDatas(@Param("dataInicio") LocalDate dataInicio, @Param("professorId") Long professorId,
                             @Param("titulacaoId") Long titulacaoId, @Param("idDoVinculoEditado") Long idDoVinculoEditado);
}
