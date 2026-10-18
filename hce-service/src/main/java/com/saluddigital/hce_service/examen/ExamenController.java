package com.saluddigital.hce_service.examen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/examenes")
public class ExamenController {

    private final ExamenRepository repository;

    public ExamenController(ExamenRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Examen> listar() {
        return repository.findAll();
    }
}