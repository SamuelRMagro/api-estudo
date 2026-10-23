package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_professor_titulacoes")
@Getter
@Setter
@NoArgsConstructor
public class ProfessorTitulacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "titulacao_id")
    private Titulacao titulacao;

    @Column(nullable = false)
    private LocalDate dataInicio;

    private LocalDate dataTermino;

    @Column(name = "vinculo", nullable = false)
    @ColumnDefault("true")
    private Boolean isAtivo = true;

    @Column(nullable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Generated(event = EventType.INSERT)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP DEFAULT NULL")
    private LocalDateTime dataAtualizacao;

    public ProfessorTitulacao(Professor professorId, Titulacao titulacaoId, LocalDate dataInicio, LocalDate dataTermino) {
        this.professor = professorId;
        this.titulacao = titulacaoId;
        this.dataInicio = dataInicio;
        this.dataTermino = dataTermino;
    }
}
