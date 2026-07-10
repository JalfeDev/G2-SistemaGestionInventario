package com.g2.demo.pruebas.juan.cajanegra;

import com.g2.demo.config.JwtUtil;
import com.g2.demo.config.RevokedTokenService;
import com.g2.demo.dto.LoginRequest;
import com.g2.demo.dto.LoginResponse;
import com.g2.demo.entity.Rol;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JuanHU01CajaNegraTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RevokedTokenService revokedTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_conPasswordIncorrecto_retornaUnauthorized() {
        Usuario usuario = usuario("juan", "Juan Perez", "GERENTE", "hash");
        when(usuarioRepository.findByUsernameOrEmail("juan", "juan"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("1234", "hash")).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest("juan", "1234")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Usuario o contrasena incorrectos", exception.getReason());
    }

    @Test
    void login_conCredencialesValidas_retornaTokenRolYNombre() {
        Usuario usuario = usuario("juan", "Juan Perez", "GERENTE", "hash");
        when(usuarioRepository.findByUsernameOrEmail("juan", "juan"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("secreto", "hash")).thenReturn(true);
        when(jwtUtil.generateToken(usuario)).thenReturn("token-jwt");

        LoginResponse response = authService.login(loginRequest("juan", "secreto"));

        assertEquals("token-jwt", response.getToken());
        assertEquals("GERENTE", response.getRol());
        assertEquals("Juan Perez", response.getNombre());
    }

    private LoginRequest loginRequest(String usuario, String contrasena) {
        LoginRequest request = new LoginRequest();
        request.setUsuario(usuario);
        request.setContrasena(contrasena);
        return request;
    }

    private Usuario usuario(String username, String nombre, String rolNombre, String password) {
        Rol rol = new Rol();
        rol.setNombre(rolNombre);

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setNombre(nombre);
        usuario.setRol(rol);
        usuario.setPassword(password);
        usuario.setActivo(true);
        return usuario;
    }
}
