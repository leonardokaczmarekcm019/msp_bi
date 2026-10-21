package com.example.msp_bi.exception;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> validacao(MethodArgumentNotValidException e) {
        List<String> erros = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(Map.of("errors", erros));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, List<String>>> conflito() {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("errors", List.of("Registro em uso por outros dados ou com valores inválidos")));
    }
}
