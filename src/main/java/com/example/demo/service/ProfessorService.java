package com.example.demo.service;

import com.example.demo.dto.professor.*;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.mappers.ProfessorMapper;
import com.example.demo.model.Professor;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.repository.TitulacaoRepository;
import com.example.demo.specifictions.ProfessorSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final TitulacaoRepository titulacaoRepository;
    private final UsuarioService usuarioService;
    private final ProfessorMapper professorMapper;

    public ProfessorService(ProfessorRepository professorRepository, TitulacaoRepository titulacaoRepository, UsuarioService usuarioService, ProfessorMapper professorMapper) {
        this.professorRepository = professorRepository;
        this.titulacaoRepository = titulacaoRepository;
        this.usuarioService = usuarioService;
        this.professorMapper = professorMapper;
    }


    public Page<ProfessorResponseDTO> listagemProfessores(Pageable pageable, @Valid ProfessorFilterRequest request) {
        Specification<Professor> spec = Specification
                .where(ProfessorSpecification.nomeContem(request.nome()))
                .and(ProfessorSpecification.emailContem(request.email()))
                .and(ProfessorSpecification.cpfContem(request.cpf()))
                .and(ProfessorSpecification.isAtivo(request.isAtivo()))
                .and(ProfessorSpecification.inicioAdmissaoDepoisDe(request.dataAdmissaoInicio()))
                .and(ProfessorSpecification.inicioAdmissaoAntesDe(request.dataAdmissaoFim()));
        Page<Professor> professorPage = professorRepository.findAll(spec, pageable);

        return professorPage.map(professorMapper::fromEntityToDTO);
    }

    public ProfessorIdResponseDTO buscarOuFalhar(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Professor de id: %d não encontrado.", id)));
        List<TitulacaoResponseDTO> titulacoes = titulacaoRepository.buscaTitulacoesProfessorId(id);
        return professorMapper.fromEntityToTitulacoes(professor, titulacoes);
    }

    @Transactional
    public ProfessorResponseDTO cadastrarProfessor(@Valid ProfessorCreateDTO createDTO) {

        UsuarioResponseDTO usuarioResponseDTO = usuarioService.cadastrarUsuario(createDTO.usuario());
        Usuario usuarioSaved = usuarioService.buscarPorId(usuarioResponseDTO.id());

        Professor professor = professorMapper.fromCreateDtoToEntity(createDTO);

        professor.setUsuario(usuarioSaved);

        return professorMapper.fromEntityToDTO(professorRepository.save(professor));
    }

    @Transactional
    public ProfessorResponseDTO atualizarProfessor(@Valid ProfessorUpdateDTO updateDTO, Long id) {
        Professor professor = buscarUsuarioAtivoOuFalhar(id);

        UsuarioResponseDTO usuarioResponseDTO = usuarioService.atualizar(updateDTO.usuario(), professor.getUsuario().getId());
        professor.setDataAdmissao(updateDTO.dataAdmissao());

        professor.setUsuario(usuarioService.buscarPorId(professor.getUsuario().getId()));
        professorRepository.save(professor);
        return professorMapper.fromEntityToDTO(professor);
    }

    private Professor buscarUsuarioAtivoOuFalhar(Long id) {
       return professorRepository.findByIdAndUsuario_IsAtivo(id, true)
               .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Usuario de id: %d não encontrado.", id)));
    }
}