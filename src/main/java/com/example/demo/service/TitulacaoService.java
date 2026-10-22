package com.example.demo.service;

import com.example.demo.dto.titulacao.TitulacaoCreateDTO;
import com.example.demo.dto.titulacao.TitulacaoFilterRequest;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.mappers.TitulacaoMapper;
import com.example.demo.model.Titulacao;
import com.example.demo.repository.TitulacaoRepository;
import com.example.demo.specifictions.TitulacaoSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TitulacaoService {

    private final TitulacaoRepository titulacaoRepository;
    private final TitulacaoMapper titulacaoMapper;

    public TitulacaoService(TitulacaoRepository titulacaoRepository, TitulacaoMapper titulacaoMapper) {
        this.titulacaoRepository = titulacaoRepository;
        this.titulacaoMapper = titulacaoMapper;
    }

    public Page<TitulacaoResponseDTO> listagemTitulacoes(Pageable pageable, @Valid TitulacaoFilterRequest request) {
        Specification<Titulacao> spec = Specification
                .where(TitulacaoSpecification.nomeContem(request.nome()))
                .and(TitulacaoSpecification.areaContem(request.area()));
        Page<Titulacao> titulacaoPage = titulacaoRepository.findAll(spec, pageable);

        return titulacaoPage.map(titulacaoMapper::fromEntityToDTO);
    }

    public TitulacaoResponseDTO buscarOuFalhar(Long id) {
        Titulacao titulacao = titulacaoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoCustomException(String.format("Titulação de id: %d não encontrada", id)));

        return titulacaoMapper.fromEntityToDTO(titulacao);
    }

    public TitulacaoResponseDTO cadastrarTitulacao(@Valid TitulacaoCreateDTO createDTO) {
        Titulacao titulacao = titulacaoMapper.fromDTOtoEntity(createDTO);
        titulacaoRepository.save(titulacao);

        return titulacaoMapper.fromEntityToDTO(titulacao);
    }
}
