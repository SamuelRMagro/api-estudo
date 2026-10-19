package com.example.demo.controller;

import com.example.demo.dto.aluno.AlunoCreateDTO;
import com.example.demo.dto.aluno.AlunoFilterRequest;
import com.example.demo.dto.aluno.AlunoResponseDTO;
import com.example.demo.dto.aluno.AlunoUpdateDTO;
import com.example.demo.service.AlunoService;
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
@RequestMapping("/alunos")
@Validated
@Tag(name = "Recurso de alunos", description = "Endpoints para o gerenciamento do recurso alunos")
public class AlunoController {
    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consulta de alunos",
            description = "Busca alunos paginados com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AlunoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<AlunoResponseDTO>> listarAlunos(
            @ParameterObject
            @PageableDefault(
                    sort = "usuario.nome",
                    direction = Sort.Direction.ASC
            ) Pageable pageable, @Valid @ParameterObject AlunoFilterRequest request){
        Page<AlunoResponseDTO> alunos = service.listagemAlunos(pageable, request);
        return ResponseEntity.ok(alunos);
    }

    @Operation(
            summary = "Busca de aluno",
            description = "Busca aluno por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscaAlunoId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarOuFalhar(id));
    }

    @Operation(
            summary = "Cadastro de aluno",
            description = "Cadastrar alunos"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(
            @Valid @RequestBody AlunoCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarAluno(createDTO));
    }

    @Operation(
            summary = "Atualização de aluno",
            description = "Altera dados do aluno por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(
            @Valid @RequestBody AlunoUpdateDTO updateDTO, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizarAluno(updateDTO, id));
    }

    @Operation(
            summary = "Inativação de usuario do aluno",
            description = "Inativa o status do usuario de aluno por Id"
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
        service.inativarAluno(id);
        return ResponseEntity.noContent().build();
    }
}
