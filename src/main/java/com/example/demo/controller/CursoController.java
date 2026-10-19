package com.example.demo.controller;

import com.example.demo.dto.curso.CursoCreateDTO;
import com.example.demo.dto.curso.CursoFilterRequest;
import com.example.demo.dto.curso.CursoResponseDTO;
import com.example.demo.dto.curso.CursoUpdateDTO;
import com.example.demo.service.CursoService;
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
@RequestMapping("/cursos")
@Validated
@Tag(name = "Recurso de cursos", description = "Endpoints para o gerenciamento do recurso cursos")
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consulta de cursos",
            description = "Busca cursos paginados com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<CursoResponseDTO>> listarCursos(
            @ParameterObject
            @PageableDefault(
                    sort = "nome",
                    direction = Sort.Direction.ASC
            ) Pageable pageable, @Valid @ParameterObject CursoFilterRequest request){
        Page<CursoResponseDTO> cursos = service.listagemCursos(pageable, request);
        return ResponseEntity.ok(cursos);
    }

    @Operation(
            summary = "Busca de curso",
            description = "Busca curso por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> buscaCursoId(@PathVariable Long id) {
        CursoResponseDTO dto = service.buscarOuFalharDTO(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Cadastro de curso",
            description = "Cadastrar cursos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CursoResponseDTO> cadastrar(
            @Valid @RequestBody CursoCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarCurso(createDTO));
    }

    @Operation(
            summary = "Atualização de curso",
            description = "Altera dados do curso por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CursoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody CursoUpdateDTO updateDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizar(updateDTO, id));
    }

    @Operation(
            summary = "Inativação de curso",
            description = "Inativa o status do curso por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PatchMapping("{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id){
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
