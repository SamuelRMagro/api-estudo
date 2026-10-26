package com.example.demo.exceptions;

import com.example.demo.dto.exception.ErroCampo;
import com.example.demo.dto.exception.ErroDTO;
import com.example.demo.exceptions.custom.CadastroInvalidoCustomException;
import com.example.demo.exceptions.custom.DadosInconsistentesCustomException;
import com.example.demo.exceptions.custom.RegistroNaoEncontradoCustomException;
import com.example.demo.exceptions.custom.VinculoJaExistenteCustomException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistroNaoEncontradoCustomException.class)
    public ResponseEntity<ErroDTO> erroNotFound(RegistroNaoEncontradoCustomException ex) {
        ErroDTO erro = new ErroDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(CadastroInvalidoCustomException.class)
    public ResponseEntity<Map<String, Object>> cadastroInvalido(CadastroInvalidoCustomException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("mensagem", ex.getMessage());
        body.put("erros", ex.getErros());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErroCampo> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ErroCampo(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ErroDTO err = new ErroDTO(HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "Existem campos inválidos",
                System.currentTimeMillis(),
                errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("mensagem", ex.getMessage());
        body.put("erros", null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(VinculoJaExistenteCustomException.class)
    public ResponseEntity<ErroDTO> vinculoExistente(VinculoJaExistenteCustomException ex) {
        ErroDTO erro = new ErroDTO(
                HttpStatus.CONFLICT.value(),
                "Erro de validação",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(DadosInconsistentesCustomException.class)
    public  ResponseEntity<ErroDTO> dadosInconsistentes(DadosInconsistentesCustomException ex) {
        ErroDTO erro = new ErroDTO(
                HttpStatus.CONFLICT.value(),
                "Erro de validação",
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }
}
