package com.saluddigital.hce_service.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    SecurityFilterChain filtroSeguridad(HttpSecurity http, JwtAuthenticationConverter jwtConverter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/error", "/api/auth/login").permitAll()
                        // Temporal: se protege con autenticación entre servicios en el paso 6c
                        .requestMatchers("/fhir/**").permitAll()

                        .requestMatchers("/api/auth/yo").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/pacientes").hasRole("RECEPCION")
                        .requestMatchers(HttpMethod.GET, "/api/pacientes", "/api/pacientes/*")
                            .hasAnyRole("RECEPCION", "MEDICO")

                        .requestMatchers(HttpMethod.POST, "/api/citas").hasRole("RECEPCION")
                        .requestMatchers(HttpMethod.PATCH, "/api/citas/*/cancelar").hasRole("RECEPCION")
                        .requestMatchers(HttpMethod.GET, "/api/citas", "/api/citas/*")
                            .hasAnyRole("RECEPCION", "MEDICO")

                        .requestMatchers(HttpMethod.GET, "/api/medicos", "/api/examenes")
                            .hasAnyRole("RECEPCION", "MEDICO")

                        .requestMatchers("/api/atenciones", "/api/atenciones/**").hasRole("MEDICO")
                        .requestMatchers("/api/ordenes/**").hasRole("MEDICO")

                        .anyRequest().denyAll())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtConverter)));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}