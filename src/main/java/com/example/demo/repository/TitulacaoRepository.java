package com.example.demo.repository;

import com.example.demo.dto.professorTitulacao.ProfessorIdTitulacoesResponseDTO;
import com.example.demo.model.Titulacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitulacaoRepository extends JpaRepository<Titulacao, Long>, JpaSpecificationExecutor<Titulacao> {

    @Query(value = """
        SELECT pt.id AS vinc_id, t.id AS titu_id, t.nome AS titu_nome,
        t.area AS titu_area, pt.data_inicio AS vinc_inicio,
        pt.data_termino AS vinc_termino, pt.vinculo AS vinc_ativo
        FROM tb_titulacoes t
        JOIN tb_professor_titulacoes pt ON pt.titulacao_id = t.id
        WHERE pt.professor_id = :professorId
        ORDER BY data_inicio DESC 
""", nativeQuery = true)
    List<ProfessorIdTitulacoesResponseDTO> buscaTitulacoesProfessorId(@Param("professorId") Long professorId);
}
