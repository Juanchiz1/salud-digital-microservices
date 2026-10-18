package com.saluddigital.hce_service.medico;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoRepository repository;

    public MedicoController(MedicoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MedicoResponse> listar() {
        return repository.findAll().stream().map(MedicoResponse::from).toList();
    }
}