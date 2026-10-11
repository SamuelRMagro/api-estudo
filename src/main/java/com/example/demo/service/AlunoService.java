package com.example.demo.service;

import com.example.demo.dto.aluno.AlunoFilterRequest;
import com.example.demo.dto.aluno.AlunoResponseDTO;
import com.example.demo.mappers.AlunoMapper;
import com.example.demo.model.Aluno;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.specifictions.AlunoSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AlunoMapper mapper;

    public AlunoService(AlunoRepository alunoRepository, AlunoMapper mapper) {
        this.alunoRepository = alunoRepository;
        this.mapper = mapper;
    }

    public Page<AlunoResponseDTO> listagemAlunos(Pageable pageable, @Valid AlunoFilterRequest request) {
        Specification<Aluno> spec = Specification
                .where(AlunoSpecification.nomeContem(request.nome()))
                .and(AlunoSpecification.emailContem(request.email()))
                .and(AlunoSpecification.cpfContem(request.cpf()))
                .and(AlunoSpecification.isAtivo(request.isAtivo()));
        Page<Aluno> alunoPage = alunoRepository.findAll(spec, pageable);

        return alunoPage.map(mapper::fromEntityToDTO);
    }
}
