package com.example.demo.mappers;

import com.example.demo.dto.disciplina.DisciplinaResponseDTO;
import com.example.demo.dto.disciplina.DisciplinaUpdateDTO;
import com.example.demo.model.Disciplina;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DisciplinaMapper {
    DisciplinaResponseDTO fromEntityToDTO(Disciplina entity);

    void fromUpdateDTOtoEntity(@MappingTarget Disciplina disciplina, DisciplinaUpdateDTO updateDTO);
}
