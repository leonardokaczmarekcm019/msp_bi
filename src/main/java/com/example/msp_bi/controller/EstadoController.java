package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Estado;
import com.example.msp_bi.repository.EstadoRepository;

@RestController
@RequestMapping("/estados")
public class EstadoController extends CrudController<Estado> {

    public EstadoController(EstadoRepository repository) {
        super(repository);
    }
}
