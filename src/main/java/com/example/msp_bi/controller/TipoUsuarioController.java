package com.example.msp_bi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msp_bi.model.TipoUsuario;
import com.example.msp_bi.repository.TipoUsuarioRepository;

@RestController
@RequestMapping("/tipos-usuario")
public class TipoUsuarioController extends CrudController<TipoUsuario> {

    public TipoUsuarioController(TipoUsuarioRepository repository) {
        super(repository);
    }
}
