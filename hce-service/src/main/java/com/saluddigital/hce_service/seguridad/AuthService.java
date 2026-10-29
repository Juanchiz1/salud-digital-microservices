package com.saluddigital.hce_service.seguridad;

import com.saluddigital.hce_service.common.CredencialesInvalidasException;
import com.saluddigital.hce_service.common.Fechas;
import com.saluddigital.hce_service.seguridad.dto.LoginRequest;
import com.saluddigital.hce_service.seguridad.dto.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.Instant;

@Service
public class AuthService {

    private static final Duration DURACION_TOKEN = Duration.ofHours(8);

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarios, PasswordEncoder encoder, JwtEncoder jwtEncoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.hashFicticio = encoder.encode("hash-para-usuarios-inexistentes");
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Usuario usuario = usuarios.findByUsername(req.username().trim().toLowerCase()).orElse(null);

        if (usuario == null || !usuario.isActivo()) {
            encoder.matches(req.password(), hashFicticio);
            throw new CredencialesInvalidasException();
        }
        if (!encoder.matches(req.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }

        Instant ahora = Instant.now();
        Instant expira = ahora.plus(DURACION_TOKEN);

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(JwtConfig.EMISOR)
                .subject(usuario.getUsername())
                .issuedAt(ahora)
                .expiresAt(expira)
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre());
        if (usuario.getMedicoId() != null) {
            claims.claim("medicoId", usuario.getMedicoId().toString());
        }

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();

        return new LoginResponse(token, "Bearer", Fechas.local(expira), usuario.getNombre(), usuario.getRol());
    }
}