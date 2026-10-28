package com.example.demo.mappers;

import com.example.demo.dto.professor.ProfessorIdResponseDTO;
import com.example.demo.dto.professor.ProfessorCreateDTO;
import com.example.demo.dto.professor.ProfessorResponseDTO;
import com.example.demo.dto.professor.ProfessorUpdateDTO;
import com.example.demo.dto.professorTitulacao.ProfessorIdTitulacoesResponseDTO;
import com.example.demo.dto.usuario.UsuarioCreateDTO;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import com.example.demo.model.Professor;
import com.example.demo.model.Usuario;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

    @Mapping(target = "profId", source = "entity.id")
    @Mapping(target = "userId", source = "entity.usuario.id")
    @Mapping(target = "userNome", source = "entity.usuario.nome")
    @Mapping(target = "userEmail", source = "entity.usuario.email")
    @Mapping(target = "userCpf", expression = "java(cpfMascarado(entity.getUsuario()))")
    @Mapping(target = "userAtivo", source = "entity.usuario.isAtivo")
    @Mapping(target = "userDataCriacao", source = "entity.usuario.dataCadastro")
    @Mapping(target = "userDataAtualizacao", source = "entity.usuario.dataAtualizacao")
    @Mapping(target = "profDataAdmissao", source = "entity.dataAdmissao")
    @Mapping(target = "titulacoes", source = "list")
    ProfessorIdResponseDTO fromEntityToTitulacoes(Professor entity, List<ProfessorIdTitulacoesResponseDTO> list);

    @Mapping(target = "usuario.nome", source = "createDTO.nome")
    @Mapping(target = "usuario.idade", source = "createDTO.idade")
    @Mapping(target = "usuario.login", source = "createDTO.login")
    @Mapping(target = "usuario.email", source = "createDTO.email")
    @Mapping(target = "usuario.cpf", source = "createDTO.cpf")
    @Mapping(target = "usuario.senha", source = "createDTO.senha")
    @Mapping(target = "usuario.isAtivo", source = "createDTO.isAtivo")
    Professor fromCreateDtoToEntity(@Valid ProfessorCreateDTO createDTO);

    @Mapping(target = "usuario.id", source = "entity.usuario.id")
    @Mapping(target = "usuario.nome", source = "entity.usuario.nome")
    @Mapping(target = "usuario.email", source = "entity.usuario.email")
    @Mapping(target = "usuario.dataCadastro", source = "entity.usuario.dataCadastro")
    @Mapping(target = "usuario.dataAtualizacao", source = "entity.usuario.dataAtualizacao")
    @Mapping(target = "usuario.isAtivo", source = "entity.usuario.isAtivo")
    @Mapping(target = "usuario.cpf", expression = "java(cpfMascarado(usuario))")
    @Mapping(target = "usuario", source = "entity.usuario")
    ProfessorResponseDTO fromEntityToDTO(Professor entity);

    default String cpfMascarado(Usuario entity) {
        return Usuario.cpfMascarado(entity.getCpf());
    }

    UsuarioCreateDTO fromProfCreateToUserCreate(ProfessorCreateDTO profCreateDTO);

    UsuarioUpdateDTO fromProfUpdateToUserUpdate(ProfessorUpdateDTO profUpdateDTO);
}