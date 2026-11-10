package com.example.msp_bi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Alerta;
import com.example.msp_bi.repository.AlertaRepository;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaRepository repository;

    public AlertaController(AlertaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Alerta> listar() {
        return repository.findAll();
    }
}