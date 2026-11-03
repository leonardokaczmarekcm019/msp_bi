package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.service.ApiService;

@RestController
@RequestMapping("/coleta")
public class ApiController {

    private final ApiService service;

    public ApiController(ApiService service) {
        this.service = service;
    }

    @PostMapping("/executar")
    public String executar() {
        return "Itens gravados: " + service.coletarTudo();
    }
}