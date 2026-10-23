package com.example.demo.mappers;

import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoResponseDTO;
import com.example.demo.model.ProfessorTitulacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfessorTitulacaoMapper {

    @Mapping(target = "professorId", source = "entity.professor.id")
    @Mapping(target = "titulacaoId", source = "entity.titulacao.id")
    ProfessorTitulacaoResponseDTO fromEntityToDTO(ProfessorTitulacao entity);
}
