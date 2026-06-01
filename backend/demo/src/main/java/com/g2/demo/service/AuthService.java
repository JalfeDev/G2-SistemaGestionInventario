package com.g2.demo.service;

import com.g2.demo.config.JwtUtil;
import com.g2.demo.dto.LoginRequest;
import com.g2.demo.dto.LoginResponse;
import com.g2.demo.dto.UsuarioResponse;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        if (isBlank(request.getUsuario()) || isBlank(request.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario y contrasena son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByUsernameOrEmail(request.getUsuario(), request.getUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos"));

        if (!passwordMatches(request.getContrasena(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        return new LoginResponse(
                jwtUtil.generateToken(usuario),
                usuario.getRol() != null ? usuario.getRol().getNombre() : null,
                usuario.getNombre()
        );
    }

    public UsuarioResponse getCurrentUser(String username) {
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        return new UsuarioResponse(usuario);
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        try {
            if (passwordEncoder.matches(rawPassword, storedPassword)) {
                return true;
            }
        } catch (IllegalArgumentException ignored) {
            // Permite datos iniciales en texto plano durante Sprint 1; migrar a BCrypt antes de produccion.
        }
        return storedPassword.equals(rawPassword);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
