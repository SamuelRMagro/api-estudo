package com.example.demo.repository;

import com.example.demo.model.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Long>, JpaSpecificationExecutor<Disciplina> {
    Page<Disciplina> findAll(Pageable pageable);
}
