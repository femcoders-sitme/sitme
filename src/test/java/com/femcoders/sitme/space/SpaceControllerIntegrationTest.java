package com.femcoders.sitme.space;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.user.Role;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integration tests for SpaceController with JWT auth")
public class SpaceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;

    @TestConfiguration
    static class MockMailConfig {
        @Bean
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        spaceRepository.deleteAll();

        if (!userRepository.existsByEmail("admin@sitme.com")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@sitme.com")
                    .password(passwordEncoder.encode("Password123.")) // новый пароль
                    .role(Role.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
        }

        jwtToken = obtainJwtTokenFromLogin();
    }

    private String obtainJwtTokenFromLogin() throws Exception {
        String loginJson = """
    {
      "identifier": "admin",
      "password": "Password123."
    }
    """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return "Bearer " + JsonPath.read(response, "$.data.token");
    }

    @Nested
    @DisplayName("GET /api/spaces")
    class GetSpaces {

        @Test
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/spaces"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        void shouldReturnListOfSpaces() throws Exception {
            Space s1 = Space.builder().name("R-001").capacity(8).type(SpaceType.ROOM).isAvailable(true).imageUrl("https://picsum.photos/seed/roomA/600/400").build();
            Space s2 = Space.builder().name("T-001").capacity(2).type(SpaceType.TABLE).isAvailable(true).imageUrl("https://picsum.photos/seed/table01/600/400").build();
            spaceRepository.save(s1);
            spaceRepository.save(s2);

            mockMvc.perform(get("/api/spaces"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("R-001")))
                    .andExpect(jsonPath("$[1].name", is("T-001")));
        }
    }

    @Nested
    @DisplayName("POST /api/spaces")
    class AddSpace {

        @Test
        void shouldCreateNewSpace() throws Exception {
            SpaceRequest request = new SpaceRequest("R-005", 4, SpaceType.ROOM, true, "https://picsum.photos/seed/roomE/600/400");

            mockMvc.perform(post("/api/spaces")
                            .header("Authorization", jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message", is("Space created successfully")))
                    .andExpect(jsonPath("$.data.name", is("R-005")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/spaces/{id}")
    class DeleteSpace {

        @Test
        void shouldDeleteSpace() throws Exception {
            Space space = Space.builder()
                    .name("T-020")
                    .capacity(4)
                    .type(SpaceType.TABLE)
                    .isAvailable(true)
                    .imageUrl("https://picsum.photos/seed/table20/600/400")
                    .build();

            Space saved = spaceRepository.save(space);

            mockMvc.perform(delete("/api/spaces/" + saved.getId())
                            .header("Authorization", jwtToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturn404IfNotExists() throws Exception {
            mockMvc.perform(delete("/api/spaces/99999")
                            .header("Authorization", jwtToken))
                    .andExpect(status().isNotFound());
        }
    }
}