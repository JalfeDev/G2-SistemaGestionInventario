package com.g2.demo.config;

import com.g2.demo.entity.Rol;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.RolRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean enabled;
    private final String adminUsername;
    private final String adminPassword;

    public DataInitializer(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed-admin.enabled}") boolean enabled,
            @Value("${app.seed-admin.username}") String adminUsername,
            @Value("${app.seed-admin.password}") String adminPassword
    ) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        Map<String, Rol> roles = Roles.VALIDOS.stream().collect(java.util.stream.Collectors.toMap(
                nombre -> nombre,
                nombre -> rolRepository.findByNombre(nombre).orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre(nombre);
                    return rolRepository.save(nuevoRol);
                })
        ));
        usuarioRepository.findAll().stream()
                .filter(usuario -> usuario.getActivo() == null)
                .forEach(usuario -> {
                    usuario.setActivo(true);
                    usuarioRepository.save(usuario);
                });
        if (!enabled || usuarioRepository.findByUsernameOrEmail(adminUsername, adminUsername).isPresent()) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(adminUsername);
        usuario.setPassword(passwordEncoder.encode(adminPassword));
        usuario.setNombres("Administrador");
        usuario.setApellidos("");
        usuario.setEmail("");
        usuario.setRol(roles.get(Roles.ADMINISTRADOR));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
}
