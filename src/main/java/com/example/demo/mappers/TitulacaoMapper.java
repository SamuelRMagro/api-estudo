package com.example.demo.mappers;

import com.example.demo.dto.titulacao.TitulacaoCreateDTO;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.dto.titulacao.TitulacaoUpdateDTO;
import com.example.demo.model.Titulacao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TitulacaoMapper {

    TitulacaoResponseDTO fromEntityToDTO(Titulacao titulacao);

    Titulacao fromDTOtoEntity(TitulacaoCreateDTO createDTO);

    void fromUpdateDTOtoEntity(@MappingTarget Titulacao titulacao, TitulacaoUpdateDTO updateDTO);
}
