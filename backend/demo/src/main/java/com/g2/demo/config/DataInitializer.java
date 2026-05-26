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
            @Value("${app.seed-admin.enabled:true}") boolean enabled,
            @Value("${app.seed-admin.username:admin}") String adminUsername,
            @Value("${app.seed-admin.password:admin123}") String adminPassword
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

        Rol rol = rolRepository.findByNombre("GERENTE").orElseGet(() -> {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre("GERENTE");
            nuevoRol.setDescripcion("Gerente del hotel");
            return rolRepository.save(nuevoRol);
        });

        Usuario usuario = new Usuario();
        usuario.setUsername(adminUsername);
        usuario.setPassword(passwordEncoder.encode(adminPassword));
        usuario.setNombre("Administrador");
        usuario.setEmail("admin@hotelpiramide.local");
        usuario.setActivo(true);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
