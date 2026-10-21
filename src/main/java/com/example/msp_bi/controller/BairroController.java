package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Bairro;
import com.example.msp_bi.repository.BairroRepository;

@RestController
@RequestMapping("/bairros")
public class BairroController extends CrudController<Bairro> {

    public BairroController(BairroRepository repository) {
        super(repository);
    }
}
