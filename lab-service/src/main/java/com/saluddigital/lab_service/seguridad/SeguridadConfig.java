package com.saluddigital.lab_service.seguridad;

import com.saluddigital.lab_service.fhir.TokenServicio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    @Order(1)
    SecurityFilterChain filtroIntegracion(HttpSecurity http, TokenServicio tokens) throws Exception {
        http
                .securityMatcher(req -> "POST".equals(req.getMethod()) && req.getRequestURI().startsWith("/fhir/"))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasAuthority("SCOPE_" + TokenServicio.ALCANCE))
                .oauth2ResourceServer(o -> o.jwt(j -> j
                        .decoder(tokens.decoder())
                        .jwtAuthenticationConverter(new JwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain filtroGeneral(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/error").permitAll()
                        // Temporal: los usuarios del laboratorio se agregan en el paso 6c-2
                        .requestMatchers("/api/**", "/fhir/**").permitAll()
                        .anyRequest().denyAll());
        return http.build();
    }
}