package com.example.demo.controller;

import com.example.demo.dto.usuario.UsuarioCreateDTO;
import com.example.demo.dto.usuario.UsuarioFilterRequest;
import com.example.demo.dto.usuario.UsuarioResponseDTO;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import com.example.demo.service.UsuarioService;
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
@RequestMapping("/usuarios")
@Validated
@Tag(name = "Recurso de usuarios", description = "Endpoints para o gerenciamento do recurso usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(
            summary = "Consulta de usuarios",
            description = "Busca usuarios paginados com filtros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuarios(
            @ParameterObject
            @PageableDefault(
                    sort = "nome",
                    direction = Sort.Direction.ASC
            )Pageable pageable, @Valid @ParameterObject UsuarioFilterRequest request){
        Page<UsuarioResponseDTO> usuarios = service.listagemUsuarios(pageable, request);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Busca de usuario",
            description = "Busca usuario por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscaUsuarioId(
            @PathVariable Long id
    ) {
        UsuarioResponseDTO dto = service.buscarOuFalhar(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Inativação de usuario",
            description = "Inativa o status do usuario por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PatchMapping("{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id){
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cadastro de usuario",
            description = "Cadastrar usuarios"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Objeto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrarUsuario(createDTO));
    }

    @Operation(
            summary = "Atualização de usuario",
            description = "Altera dados do usuario por Id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Existem campo(s) inválido(s) na requisição", content = @Content),
            @ApiResponse(responseCode = "404", description = "Objeto não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @Valid @RequestBody UsuarioUpdateDTO updateDTO, @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.atualizar(updateDTO, id));
    }
}
