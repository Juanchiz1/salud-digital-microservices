package com.saluddigital.hce_service.seguridad;

import com.saluddigital.hce_service.seguridad.dto.LoginRequest;
import com.saluddigital.hce_service.seguridad.dto.LoginResponse;
import com.saluddigital.hce_service.seguridad.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;
    private final UsuarioActual usuario;

    public AuthController(AuthService auth, UsuarioActual usuario) {
        this.auth = auth;
        this.usuario = usuario;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @GetMapping("/yo")
    public UsuarioResponse yo() {
        return new UsuarioResponse(usuario.username(), usuario.nombre(), usuario.rol(), usuario.medicoId());
    }
}