package com.example.demo.mappers;

import com.example.demo.dto.aluno.AlunoCreateDTO;
import com.example.demo.dto.aluno.AlunoResponseDTO;
import com.example.demo.model.Aluno;
import com.example.demo.model.Usuario;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlunoMapper {
    @Mapping(target = "usuario", source = "entity.usuario")
    @Mapping(target = "usuario.cpf", expression = "java(cpfMascarado(usuario))")
    AlunoResponseDTO fromEntityToDTO(Aluno entity);

    default String cpfMascarado(Usuario usuario) {
        return Usuario.cpfMascarado(usuario.getCpf());
    }

    @Mapping(target = "usuario", source = "createDTO.usuario")
    Aluno fromCreateDtoToEntity(@Valid AlunoCreateDTO createDTO);
}
