package com.g2.demo.pruebas.diago.cajanegra;

import com.g2.demo.config.JwtUtil;
import com.g2.demo.config.RevokedTokenService;
import com.g2.demo.controller.UsuarioController;
import com.g2.demo.dto.UsuarioRequest;
import com.g2.demo.repository.UsuarioRepository;
import com.g2.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerCajaNegraTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper; 

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private RevokedTokenService revokedTokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void crear_conUsernameDuplicado_retorna409() throws Exception {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("jgarcia");
        request.setPassword("Almacen2026");
        request.setNombres("Jose");
        request.setApellidos("Garcia");
        request.setEmail("jgarcia@hotelpiramide.com");
        request.setRolId(3L);

        when(usuarioService.crear(any(UsuarioRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}