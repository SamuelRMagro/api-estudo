package com.example.demo.controller;

import com.example.demo.dto.titulacao.TitulacaoCreateDTO;
import com.example.demo.dto.titulacao.TitulacaoFilterRequest;
import com.example.demo.dto.titulacao.TitulacaoResponseDTO;
import com.example.demo.dto.titulacao.TitulacaoUpdateDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(
            summary = "Busca de titulacao",
            description = "Busca titulacao por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TitulacaoResponseDTO> buscaTitulacaoId(@PathVariable Long id) {
        return ResponseEntity.ok(titulacaoService.buscarOuFalhar(id));
    }

    @Operation(
            summary = "Cadastro de titulacao",
            description = "Cadastrar titulacoes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TitulacaoResponseDTO> cadastrar(
            @Valid @RequestBody TitulacaoCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(titulacaoService.cadastrarTitulacao(createDTO));
    }

    @Operation(
            summary = "Atualização de titulacao",
            description = "Altera dados da titulacao por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TitulacaoResponseDTO> atualizar(
            @Valid @RequestBody TitulacaoUpdateDTO updateDTO, @PathVariable Long id) {
        return ResponseEntity.ok(titulacaoService.atualizar(updateDTO, id));
    }

    @Operation(
            summary = "Inativação de titulacao",
            description = "Inativa o status do titulacao por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        titulacaoService.inativar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
