package com.example.demo.controller;

import com.example.demo.dto.professor.*;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoCreateDTO;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoDatasUpdateDTO;
import com.example.demo.dto.professorTitulacao.ProfessorTitulacaoResponseDTO;
import com.example.demo.service.ProfessorService;
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
@RequestMapping("/professores")
@Validated
@Tag(name = "Recurso de professores", description = "Endpoints para o gerenciamento do recurso professores")
public class ProfessorController {

    private final ProfessorService service;

    public ProfessorController(ProfessorService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consulta de professores",
            description = "Busca professores paginados com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<ProfessorResponseDTO>> listarProfessores(
            @ParameterObject
            @PageableDefault(
                    sort = "usuario.nome",
                    direction = Sort.Direction.ASC
            )Pageable pageable, @Valid @ParameterObject ProfessorFilterRequest request) {
        Page<ProfessorResponseDTO> professores = service.listagemProfessores(pageable, request);
        return ResponseEntity.ok(professores);
    }

    @Operation(
            summary = "Busca de professor",
            description = "Busca professor por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorIdResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorIdResponseDTO> buscaProfessorId(
            @PathVariable Long id){
        ProfessorIdResponseDTO dto = service.buscarOuFalhar(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Cadastro de professor",
            description = "Cadastrar professores"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> cadastrar(
            @Valid @RequestBody ProfessorCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarProfessor(createDTO));
    }

    @Operation(
            summary = "Atualização de professor",
            description = "Altera dados do professor por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizar(
            @Valid @RequestBody ProfessorUpdateDTO updateDTO, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizarProfessor(updateDTO, id));
    }

    @Operation(
            summary = "Inativação de usuario do professor",
            description = "Inativa o status do usuario de professor por Id"
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
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cria relacionamento",
            description = "Cria relacionamento entre professor e titulacao"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorTitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Vínculo já existente", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping("/{id}/titulacoes")
    public ResponseEntity<ProfessorTitulacaoResponseDTO> relacionamentoProfessorTitulacao(
            @PathVariable Long id, @Valid @RequestBody ProfessorTitulacaoCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criaRelacionamentoProfTitu(createDTO, id));
    }

    @Operation(
            summary = "Atualização de vínculo",
            description = "Atualiza datas do vínculo por id de professor e de vínculo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProfessorTitulacaoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}/titulacoes/{vinculoId}")
    public ResponseEntity<ProfessorTitulacaoResponseDTO> atualizaDatasVinculo(
            @PathVariable Long id, @PathVariable Long vinculoId,
            @Valid @RequestBody ProfessorTitulacaoDatasUpdateDTO datasUpdateDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizarDatas(id, vinculoId, datasUpdateDTO));
    }

    @Operation(
            summary = "Inativação do vículo",
            description = "Inativa o status do vínculo por id de professor e de vínculo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PatchMapping("/{id}/titulacoes/{vinculoId}/inativar")
    public ResponseEntity<Void> inativarVinculo(
            @PathVariable Long id, @PathVariable Long vinculoId) {
        service.inativarVinculo(id, vinculoId);
        return ResponseEntity.noContent().build();
    }
}