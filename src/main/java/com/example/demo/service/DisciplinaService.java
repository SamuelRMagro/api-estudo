package com.example.demo.service;

import com.example.demo.dto.disciplina.DisciplinaCreateDTO;
import com.example.demo.dto.disciplina.DisciplinaFilterRequest;
import com.example.demo.dto.disciplina.DisciplinaResponseDTO;
import com.example.demo.dto.disciplina.DisciplinaUpdateDTO;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.mappers.DisciplinaMapper;
import com.example.demo.model.Disciplina;
import com.example.demo.repository.DisciplinaRepository;
import com.example.demo.specifictions.DisciplinaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaMapper disciplinaMapper;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, DisciplinaMapper disciplinaMapper) {
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaMapper = disciplinaMapper;
    }

    public Page<DisciplinaResponseDTO> listarDisciplinas(Pageable pageable, DisciplinaFilterRequest request) {
        Specification<Disciplina> spec = Specification
                .where(DisciplinaSpecification.nomeContem(request.nome()))
                .and(DisciplinaSpecification.ementaContem(request.ementa()))
                .and(DisciplinaSpecification.isAtivo(request.isAtivo()))
                .and(DisciplinaSpecification.cargaHorariaAte(request.cargaHoraria()));

        Page<Disciplina> disciplinaPage = disciplinaRepository.findAll(spec, pageable);
        return disciplinaPage.map(disciplinaMapper::fromEntityToDTO);
    }

    public DisciplinaResponseDTO buscarOuFalhar(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Disciplina de id: %d não encontrado", id)));

        return disciplinaMapper.fromEntityToDTO(disciplina);
    }

    public DisciplinaResponseDTO cadastrarDisciplina(DisciplinaCreateDTO createDTO) {
        Disciplina disciplina = new Disciplina(createDTO.nome(), createDTO.ementa(), createDTO.cargaHoraria());

        disciplinaRepository.save(disciplina);

        return disciplinaMapper.fromEntityToDTO(disciplina);
    }

    private Disciplina buscarOuFalharEntity(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Disciplina de id: %d não encontrado", id)));
    }

    public DisciplinaResponseDTO atualizarCurso(Long id, DisciplinaUpdateDTO updateDTO) {
        Disciplina disciplina = buscarOuFalharEntity(id);
        disciplinaMapper.fromUpdateDTOtoEntity(disciplina, updateDTO);
        disciplina.setDataAtualizacao(LocalDateTime.now());

        disciplinaRepository.save(disciplina);
        return disciplinaMapper.fromEntityToDTO(disciplina);
    }
}
