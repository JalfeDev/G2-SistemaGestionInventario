package com.g2.demo.config;

import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RevokedTokenService revokedTokenService;
    private final UsuarioRepository usuarioRepository;

    public JwtFilter(JwtUtil jwtUtil, RevokedTokenService revokedTokenService, UsuarioRepository usuarioRepository) {
        this.jwtUtil = jwtUtil;
        this.revokedTokenService = revokedTokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/api/auth/login".equals(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.isValid(token) || revokedTokenService.isRevoked(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT invalido o expirado");
            return;
        }

        String username = jwtUtil.getUsername(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username).orElse(null);
            if (usuario == null || Boolean.FALSE.equals(usuario.getActivo())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario desactivado o inexistente");
                return;
            }
            if (usuario.getRol() == null || !Roles.VALIDOS.contains(usuario.getRol().getNombre())) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario sin rol autorizado");
                return;
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities(usuario.getRol().getNombre())
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    private List<SimpleGrantedAuthority> authorities(String rol) {
        if (rol == null || rol.isBlank()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
    }
}
