package com.example.demo.controller;

import com.example.demo.dto.titulacao.TitulacaoFilterRequest;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.service.TitulacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/titulacoes")
@Validated
@Tag(name = "Recurso de titulacoes", description = "Endpoints para o gerenciamento do recurso titulacoes")
public class TitulacaoController {
    private final TitulacaoService titulacaoService;

    public TitulacaoController(TitulacaoService titulacaoService) {
        this.titulacaoService = titulacaoService;
    }

    @Operation(
            summary = "Consulta de titulacoes",
            description = "Busca titulacoes paginadas com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<TitulacaoResponseDTO>> listarTitulcoes(
            @ParameterObject
            @PageableDefault(
                    sort = "nome",
                    direction = Sort.Direction.ASC
            ) Pageable pageable, @Valid @ParameterObject TitulacaoFilterRequest request) {
        Page<TitulacaoResponseDTO> titulacoes = titulacaoService.listagemTitulacoes(pageable, request);
        return ResponseEntity.ok(titulacoes);
    }
}
