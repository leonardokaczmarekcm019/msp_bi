package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Local;
import com.example.msp_bi.repository.LocalRepository;

@RestController
@RequestMapping("/locais")
public class LocalController extends CrudController<Local> {

    public LocalController(LocalRepository repository) {
        super(repository);
    }
}
