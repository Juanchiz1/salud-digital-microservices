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
import com.saluddigital.hce_service.integracion.TokenServicio;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

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
    SecurityFilterChain filtroSeguridad(HttpSecurity http, JwtAuthenticationConverter jwtConverter) throws Exception {
               http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/error", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/fhir/ServiceRequest/*").hasRole("MEDICO")

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

                        
                        .requestMatchers(HttpMethod.POST, "/api/accesos-emergencia").hasRole("MEDICO")
                        .requestMatchers("/api/auditoria", "/api/auditoria/**").hasRole("AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/indicadores").hasRole("GERENCIA")

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

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${cors.origenes}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}