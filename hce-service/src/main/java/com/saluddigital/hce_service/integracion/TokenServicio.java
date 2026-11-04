package com.saluddigital.hce_service.integracion;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

@Component
public class TokenServicio {

    public static final String ALCANCE = "fhir";
    private static final Duration DURACION = Duration.ofMinutes(2);

    private final String nombre;
    private final String par;
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public TokenServicio(@Value("${integracion.nombre}") String nombre,
                         @Value("${integracion.par}") String par,
                         @Value("${integracion.secreto}") String secreto) {
        this.nombre = nombre;
        this.par = par;
        SecretKey clave = clave(secreto);
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(clave));

        NimbusJwtDecoder d = NimbusJwtDecoder.withSecretKey(clave).macAlgorithm(MacAlgorithm.HS256).build();
        d.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(par),
                new JwtClaimValidator<Object>(JwtClaimNames.AUD,
                        aud -> aud instanceof Collection<?> c && c.contains(nombre))));
        this.decoder = d;
    }

    /** Token para llamar al otro servicio. Se genera uno nuevo en cada petición. */
    public String emitir() {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(nombre)
                .subject(nombre)
                .audience(List.of(par))
                .issuedAt(ahora)
                .expiresAt(ahora.plus(DURACION))
                .claim("scope", ALCANCE)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /** Valida los tokens que llegan del otro servicio. */
    public JwtDecoder decoder() {
        return decoder;
    }

    private static SecretKey clave(String secreto) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(secreto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("INTEGRACION_SECRET no es un valor Base64 válido", e);
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("INTEGRACION_SECRET debe tener al menos 256 bits (32 bytes)");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}