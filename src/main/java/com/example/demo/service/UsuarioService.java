package com.example.demo.service;

import com.example.demo.dto.exception.ErroCampo;
import com.example.demo.dto.usuario.UsuarioCreateDTO;
import com.example.demo.dto.usuario.UsuarioFilterRequest;
import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import com.example.demo.exceptions.custom.CadastroInvalidoCustomException;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.mappers.UsuarioMapper;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.specifictions.UsuarioSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper mapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
    }

    private String limpaCpf(String cpf){
        if ( cpf == null) return null;

        String limpo = cpf.replaceAll("\\D", "");

        if (limpo.length() != 11){
            return null;
        }

        return limpo;
    }

    public Page<UsuarioResponseDTO> listagemUsuarios(Pageable pageable, UsuarioFilterRequest request) {
        Specification<Usuario> spec = Specification
                .where(UsuarioSpecification.nomeContem(request.nome()))
                .and(UsuarioSpecification.emailContem(request.email()))
                .and(UsuarioSpecification.isAtivo(request.isAtivo()))
                .and(UsuarioSpecification.inicioDepoisDe(request.dataCadastroInicio()))
                .and(UsuarioSpecification.inicioAntesDe(request.dataCadastroFim()));
        Page<Usuario> usuarioPage = usuarioRepository.findAll(spec, pageable);

        return usuarioPage.map(mapper::fromEntityToDTO);
    }

    public UsuarioResponseDTO buscarOuFalhar(Long id){
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Usuário de id: %d não encontrado.", id)));

        return mapper.fromEntityToDTO(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Usuário de id: %d não encontrado.", id)));
    }

    public void inativar(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()){
            throw new RegistroNaoEncontradoCustomException(String.format("Usuário de id: %d não encontrado.", id));
        } else if(usuario.get().inativo()) {
            return;
        } else {
            Usuario user = usuario.get();
            user.setIsAtivo(false);
            user.setDataAtualizacao(LocalDateTime.now());
            user.setDataInativacao(LocalDateTime.now());
            usuarioRepository.save(user);
        }
    }

    public UsuarioResponseDTO cadastrarUsuario(UsuarioCreateDTO dto) {
         Usuario usuario = new Usuario(dto.isAtivo(), dto.email(), dto.login(), dto.idade(), limpaCpf(dto.cpf()), dto.nome(), dto.senha());

         List<ErroCampo> erros = new ArrayList<>();

         if (usuarioRepository.existsByEmail(usuario.getEmail())){
             erros.add(new ErroCampo("email", "E-mail já cadastrado."));
         }
         if (usuarioRepository.existsByCpf(usuario.getCpf())){
             erros.add(new ErroCampo("cpf", "CPF já cadastrado."));
         }
         if (usuarioRepository.existsByLogin(usuario.getLogin())){
             erros.add(new ErroCampo("login", "Login já cadastrado."));
         }

         if (!erros.isEmpty()){
             throw new CadastroInvalidoCustomException(erros);
         }

         usuario = usuarioRepository.save(usuario);
         return mapper.fromEntityToDTO(usuario);
    }

    public UsuarioResponseDTO atualizar(@Valid UsuarioUpdateDTO updateDTO, Long id) {
        Optional<Usuario> usuario = usuarioRepository.findByIdAndIsAtivoIsTrue(id);

        List<ErroCampo> erros = new ArrayList<>();

        if (usuario.isEmpty()){
            throw new RegistroNaoEncontradoCustomException(String.format("Usuário de id: %d não foi encontrado ou está inativo", id));
        }  else {
            Usuario user = usuario.get();
            user.setNome(updateDTO.nome());
            user.setIdade(updateDTO.idade());

            if (usuarioRepository.existsByEmailAndIdNot(updateDTO.email(), id)){
                erros.add(new ErroCampo("email", "E-mail já cadastrado."));
            } else {
                user.setEmail(updateDTO.email());
            }
            if (usuarioRepository.existsByCpfAndIdNot(limpaCpf(updateDTO.cpf()), id)){
                erros.add(new ErroCampo("cpf", "CPF já cadastrado."));
            } else {
                user.setCpf(limpaCpf(updateDTO.cpf()));
            }

            if (!erros.isEmpty()){
                throw new CadastroInvalidoCustomException(erros);
            }

            user.setDataAtualizacao(LocalDateTime.now());
            usuarioRepository.save(user);

            return mapper.fromEntityToDTO(user);
        }
    }
}
