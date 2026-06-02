package com.g2.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final String frontendUrl;

    public SecurityConfig(
            JwtFilter jwtFilter,
            @Value("${FRONTEND_URL:http://localhost:5173}") String frontendUrl
    ) {
        this.jwtFilter = jwtFilter;
        this.frontendUrl = frontendUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/usuarios/**", "/api/roles/**", "/api/categorias/**",
                                "/api/unidades-medida/**", "/api/reportes/**")
                        .hasAnyRole("GERENTE", "ADMINISTRADOR")
                        .requestMatchers("/api/ingresos-inventario/**", "/api/detalles-ingreso/**",
                                "/api/proveedores/**", "/api/proveedor-producto/**")
                        .hasAnyRole("GERENTE", "ADMINISTRADOR", "ALMACEN")
                        .requestMatchers("/api/distribuciones-insumos/**", "/api/detalles-distribucion/**",
                                "/api/habitaciones/**")
                        .hasAnyRole("GERENTE", "ADMINISTRADOR", "HOUSEKEEPING")
                        .requestMatchers("/api/movimientos-inventario/**")
                        .hasAnyRole("GERENTE", "ADMINISTRADOR", "ALMACEN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.stream(frontendUrl.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isBlank())
                        .map(SecurityConfig::normalizeOrigin)
                        .distinct()
                        .toList()
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static String normalizeOrigin(String origin) {
        String normalized = origin.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }

        String lower = normalized.toLowerCase();
        String scheme = lower.startsWith("localhost") || lower.startsWith("127.0.0.1")
                ? "http://"
                : "https://";
        return scheme + normalized;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
