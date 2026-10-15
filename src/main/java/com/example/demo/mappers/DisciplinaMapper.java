package com.example.demo.mappers;

import com.example.demo.dto.disciplina.DisciplinaResponseDTO;
import com.example.demo.model.Disciplina;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DisciplinaMapper {
    DisciplinaResponseDTO fromEntityToDTO(Disciplina entity);
}
