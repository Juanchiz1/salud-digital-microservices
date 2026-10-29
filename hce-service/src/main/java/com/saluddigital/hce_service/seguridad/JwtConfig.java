package com.saluddigital.hce_service.seguridad;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    public static final String EMISOR = "saluddigital-hce";

    @Bean
    SecretKey jwtKey(@Value("${seguridad.jwt.secreto}") String secreto) {
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(secreto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("JWT_SECRET no es un valor Base64 válido", e);
        }
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 256 bits (32 bytes)");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(EMISOR));
        return decoder;
    }
}