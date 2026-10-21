package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.Organizacao;
import com.example.msp_bi.repository.OrganizacaoRepository;

@RestController
@RequestMapping("/organizacoes")
public class OrganizacaoController extends CrudController<Organizacao> {

    public OrganizacaoController(OrganizacaoRepository repository) {
        super(repository);
    }
}
