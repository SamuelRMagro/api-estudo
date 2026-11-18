package com.example.demo.mappers;

import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.utils.IdadeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "cpf", expression = "java(cpfMascarado(entity))")
    @Mapping(target = "idade", expression = "java(calculaIdade(entity))")
    UsuarioResponseDTO fromEntityToDTO(Usuario entity);

    default String cpfMascarado(Usuario entity) {
        return Usuario.cpfMascarado(entity.getCpf());
    }

    default Integer calculaIdade(Usuario entity) {
        if (entity.getAluno() != null) {
            return IdadeUtil.calculaIdade(entity.getAluno().getDataNascimento());
        }
        return entity.getIdade();
    }
}
