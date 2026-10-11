package com.example.demo.mappers;

import com.example.demo.dto.professor.ProfessorCreateDTO;
import com.example.demo.dto.professor.ProfessorIdResponseDTO;
import com.example.demo.dto.professor.ProfessorResponseDTO;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.model.Professor;
import com.example.demo.model.Usuario;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfessorMapper {

    @Mapping(target = "usuarioId", source = "entity.usuario.id")
    @Mapping(target = "nome", source = "entity.usuario.nome")
    @Mapping(target = "email", source = "entity.usuario.email")
    @Mapping(target = "cpf", expression = "java(cpfMascarado(entity.getUsuario()))")
    @Mapping(target = "dataCriacao", source = "entity.usuario.dataCadastro")
    @Mapping(target = "dataAtualizacao", source = "entity.usuario.dataAtualizacao")
    @Mapping(source = "list", target = "titulacoes")
    @Mapping(target = "ativo", source = "entity.usuario.isAtivo")
    ProfessorIdResponseDTO fromEntityToTitulacoes(Professor entity, List<TitulacaoResponseDTO> list);

    @Mapping(target = "usuario", source = "createDTO.usuario")
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
}