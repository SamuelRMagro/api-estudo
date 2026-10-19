package com.example.demo.service;

import com.example.demo.dto.aluno.AlunoCreateDTO;
import com.example.demo.dto.aluno.AlunoFilterRequest;
import com.example.demo.dto.aluno.AlunoResponseDTO;
import com.example.demo.dto.aluno.AlunoUpdateDTO;
import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.mappers.AlunoMapper;
import com.example.demo.model.Aluno;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.specifictions.AlunoSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final AlunoMapper mapper;
    private final UsuarioService usuarioService;
    private final AlunoMapper alunoMapper;

    public AlunoService(AlunoRepository alunoRepository, AlunoMapper mapper, UsuarioService usuarioService, AlunoMapper alunoMapper) {
        this.alunoRepository = alunoRepository;
        this.mapper = mapper;
        this.usuarioService = usuarioService;
        this.alunoMapper = alunoMapper;
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

    public AlunoResponseDTO buscarOuFalhar(Long id) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Aluno de id: %d não encontrado", id)));
        return mapper.fromEntityToDTO(aluno);
    }

    @Transactional
    public AlunoResponseDTO cadastrarAluno(@Valid AlunoCreateDTO createDTO) {
        UsuarioResponseDTO usuarioResponseDTO = usuarioService.cadastrarUsuario(createDTO.usuario());
        Usuario usuarioSaved = usuarioService.buscarPorId(usuarioResponseDTO.id());

        Aluno aluno = alunoMapper.fromCreateDtoToEntity(createDTO);
        aluno.setUsuario(usuarioSaved);
        aluno.getUsuario().setIdade(calculaIdade(createDTO.dataNascimento()));
        aluno.setTelefone(createDTO.telefone().replaceAll("\\D", ""));

        return alunoMapper.fromEntityToDTO(alunoRepository.save(aluno));
    }

    private Integer calculaIdade(LocalDate nascimento) {
        LocalDate hoje = LocalDate.now();

        return Period.between(nascimento, hoje).getYears();
    }

    @Transactional
    public AlunoResponseDTO atualizarAluno(@Valid AlunoUpdateDTO updateDTO, Long id) {
        Aluno aluno = buscarUsuarioAtivoOuFalhar(id);

        var usuarioResponseDTO = usuarioService.atualizar(updateDTO.usuario(), aluno.getUsuario().getId());

        aluno.setDataNascimento(updateDTO.dataNascimento());
        aluno.getUsuario().setIdade(calculaIdade(updateDTO.dataNascimento()));
        aluno.setTelefone(StringUtils.hasText(updateDTO.telefone()) ? updateDTO.telefone().trim() : null);

        return alunoMapper.fromEntityToDTO(alunoRepository.save(aluno));
    }

    private Aluno buscarUsuarioAtivoOuFalhar(Long id) {
        return alunoRepository.findByIdAndUsuario_IsAtivo(id, true)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Aluno de id: %d não encontrado.", id)));
    }
}
