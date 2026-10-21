package com.example.demo.mappers;

import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.model.Titulacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TitulacaoMapper {

    TitulacaoResponseDTO fromEntityToDTO(Titulacao titulacao);
}
