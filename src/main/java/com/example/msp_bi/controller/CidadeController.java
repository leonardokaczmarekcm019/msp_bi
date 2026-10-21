package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Cidade;
import com.example.msp_bi.repository.CidadeRepository;

@RestController
@RequestMapping("/cidades")
public class CidadeController extends CrudController<Cidade> {

    public CidadeController(CidadeRepository repository) {
        super(repository);
    }
}
