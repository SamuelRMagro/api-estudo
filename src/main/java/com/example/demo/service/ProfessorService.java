package com.example.demo.service;

import com.example.demo.dto.professor.*;
import com.example.demo.dto.professorTitulacao.ProfessorIdTitulacoesResponseDTO;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoCreateDTO;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoDatasUpdateDTO;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoResponseDTO;
import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.exceptions.custom.DadosInconsistentesCustomException;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.exceptions.custom.VinculoJaExistenteCustomException;
import com.example.demo.mappers.ProfessorMapper;
import com.example.demo.mappers.ProfessorTitulacaoMapper;
import com.example.demo.model.Professor;
import com.example.demo.model.ProfessorTitulacao;
import com.example.demo.model.Titulacao;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ProfessorRepository;
import com.example.demo.repository.ProfessorTitulacaoRepository;
import com.example.demo.repository.TitulacaoRepository;
import com.example.demo.specifictions.ProfessorSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final TitulacaoRepository titulacaoRepository;
    private final UsuarioService usuarioService;
    private final ProfessorMapper professorMapper;
    private final ProfessorTitulacaoRepository professorTitulacaoRepository;
    private final ProfessorTitulacaoMapper professorTitulacaoMapper;

    public ProfessorService(ProfessorRepository professorRepository, TitulacaoRepository titulacaoRepository, UsuarioService usuarioService, ProfessorMapper professorMapper, ProfessorTitulacaoRepository professorTitulacaoRepository, ProfessorTitulacaoMapper professorTitulacaoMapper) {
        this.professorRepository = professorRepository;
        this.titulacaoRepository = titulacaoRepository;
        this.usuarioService = usuarioService;
        this.professorMapper = professorMapper;
        this.professorTitulacaoRepository = professorTitulacaoRepository;
        this.professorTitulacaoMapper = professorTitulacaoMapper;
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
        List<ProfessorIdTitulacoesResponseDTO> titulacoes = titulacaoRepository.buscaTitulacoesProfessorId(id);
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
               .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Professor de id: %d não encontrado ou inativo.", id)));
    }

    @Transactional
    public void inativar(Long id) {
        Professor professor = professorRepository.findById(id)
                        .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Professor de id: %d não encontrado.", id)));
        usuarioService.inativar(professor.getUsuario().getId());
    }

    @Transactional
    public ProfessorTitulacaoResponseDTO criaRelacionamentoProfTitu(ProfessorTitulacaoCreateDTO createDTO, Long id) {

        Professor professor = buscaProfessor(id);
        Titulacao titulacao = buscarTitulacao(createDTO.titulacaoId());
        verificarVinculosProfTitu(createDTO.dataInicio(), id, titulacao.getId());

        ProfessorTitulacao vinculoSalvo = professorTitulacaoRepository.save(new ProfessorTitulacao(professor, titulacao, createDTO.dataInicio(),
                createDTO.dataTermino()));
        return professorTitulacaoMapper.fromEntityToDTO(vinculoSalvo);
    }

    private Titulacao buscarTitulacao(Long id) {
        return titulacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Titulação de id: %d não encontrada.", id)));
    }

    private void verificarVinculosProfTitu(LocalDate dataInicio, Long professorId, Long titulacaoId) {

        BigInteger vinculos = professorTitulacaoRepository.quantVinculos(dataInicio, professorId, titulacaoId);

        if (vinculos.intValue() > 0) {
            throw new VinculoJaExistenteCustomException();
        }
    }

    @Transactional
    public ProfessorTitulacaoResponseDTO atualizarDatas(Long id, Long vinculoId, @Valid ProfessorTitulacaoDatasUpdateDTO datasUpdateDTO) {

        Professor professor = buscaProfessor(id);
        ProfessorTitulacao vinculo = buscaVinculo(vinculoId);
        Titulacao titulacao = buscarTitulacao(vinculo.getTitulacao().getId());

        if (!(vinculo.getProfessor().getId().equals(id))) {
            throw new DadosInconsistentesCustomException(String.format("O professor de id: %d não tem o vinculo de id: %d", id, vinculo.getId()));
        }

        verificarVinculosParaDatas(datasUpdateDTO.vincDataInicio(), id, titulacao.getId());

        vinculo.setDataInicio(datasUpdateDTO.vincDataInicio());
        vinculo.setDataTermino(datasUpdateDTO.vincDataTermino());
        vinculo.setDataAtualizacao(LocalDateTime.now());

        ProfessorTitulacao vinculoAtualizado = professorTitulacaoRepository.save(vinculo);
        return professorTitulacaoMapper.fromEntityToDTO(vinculoAtualizado);
    }

    private void verificarVinculosParaDatas(LocalDate dataInicio, Long professorId, Long titulacaoId) {

        BigInteger vinculos = professorTitulacaoRepository.quantVinculos(dataInicio, professorId, titulacaoId);

        if (vinculos.intValue() > 1) {
            throw new VinculoJaExistenteCustomException();
        }
    }

    private Professor buscaProfessor(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Professor de id: %d não encontrado.", id)));
    }

    private ProfessorTitulacao buscaVinculo(Long id) {
        return professorTitulacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Vinculo de id: %d não encontrado.", id)));
    }

    @Transactional
    public void inativarVinculo(Long id, Long vinculoId) {

        Professor professor = buscaProfessor(id);
        ProfessorTitulacao vinculo = buscaVinculo(vinculoId);

        if (!(vinculo.getProfessor().getId() == id)) {
            throw new DadosInconsistentesCustomException(String.format("O professor de id: %d não tem o vinculo de id: %d", id, vinculo.getId()));
        }

        if (vinculo.getIsAtivo()){
            vinculo.setDataAtualizacao(LocalDateTime.now());
            vinculo.setIsAtivo(false);
        }

        professorTitulacaoRepository.save(vinculo);
    }
}