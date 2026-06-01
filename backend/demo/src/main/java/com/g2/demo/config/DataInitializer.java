package com.g2.demo.config;

import com.g2.demo.entity.Rol;
import com.g2.demo.entity.Usuario;
import com.g2.demo.repository.RolRepository;
import com.g2.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        if (!enabled || usuarioRepository.findByUsernameOrEmail(adminUsername, adminUsername).isPresent()) {
            return;
        }

        //Crear el ron de gerente en la tabla rol si no hay uno con el rol de GERENTE
        Rol rol = rolRepository.findByNombre("GERENTE").orElseGet(() -> {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre("GERENTE");
            return rolRepository.save(nuevoRol);
        });

        //Crear un nuevo usuario en la tabla usuario si no hay uno con el rol de GERENTE
        Usuario usuario = new Usuario();
        usuario.setUsername(adminUsername);
        usuario.setPassword(passwordEncoder.encode(adminPassword));
        usuario.setNombres("Administrador");
        usuario.setApellidos("");
        usuario.setEmail("");
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
