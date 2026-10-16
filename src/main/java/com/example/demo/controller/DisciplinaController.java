package com.example.demo.controller;

import com.example.demo.dto.disciplina.DisciplinaFilterRequest;
import com.example.demo.dto.disciplina.DisciplinaResponseDTO;
import com.example.demo.service.DisciplinaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/disciplinas")
@Tag(name = "Recurso de disciplinas", description = "Endpoints para o gerenciamento do recurso disciplinas")
public class DisciplinaController {

    private final DisciplinaService service;

    public DisciplinaController(DisciplinaService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consulta de disciplinas",
            description = "Busca disciplinas paginadas com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DisciplinaResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<DisciplinaResponseDTO>> listarDisciplinas(
            @ParameterObject
            @PageableDefault(
                    sort = "nome",
                    direction = Sort.Direction.ASC
            ) Pageable pageable, @ParameterObject DisciplinaFilterRequest request) {
        Page<DisciplinaResponseDTO> disciplinas = service.listarDisciplinas(pageable, request);
        return ResponseEntity.ok(disciplinas);
    }

    @Operation(
            summary = "Consulta de disciplina",
            description = "Busca disciplina por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisciplinaResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponseDTO> buscaDisciplinaId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarOuFalhar(id));
    }
}
