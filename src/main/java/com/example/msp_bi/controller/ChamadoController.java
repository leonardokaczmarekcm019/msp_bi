package com.example.msp_bi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.msp_bi.model.Chamado;
import com.example.msp_bi.repository.ChamadoRepository;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoRepository repository;

    public ChamadoController(ChamadoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Chamado> listar() {
        return repository.findAll();
    }

}