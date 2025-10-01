package com.femcoders.sitme.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.email.EmailService;
import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {
        @Bean
        EmailService emailService() {
            return Mockito.mock(EmailService.class);
        }
    }

    private String userToken;
    private String adminToken;
    private Space testSpace;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        reservationRepository.deleteAll();
        spaceRepository.deleteAll();
        userRepository.deleteAll();

        testSpace = spaceRepository.save(
                Space.builder()
                        .name("R-001")
                        .capacity(5)
                        .type(SpaceType.ROOM)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        testUser = userRepository.save(
                User.builder()
                        .username("testuser")
                        .email("testuser@sitme.com")
                        .password(passwordEncoder.encode("Password123."))
                        .role(Role.USER)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        User admin = userRepository.save(
                User.builder()
                        .username("admin")
                        .email("admin@sitme.com")
                        .password(passwordEncoder.encode("Password123."))
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        userToken = obtainJwtToken("testuser", "Password123.");
        adminToken = obtainJwtToken("admin", "Password123.");
    }

    private String obtainJwtToken(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"" + username + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("data").get("token").asText();
    }

    @Nested
    @DisplayName("GET /api/reservations")
    class GetReservations {

        @Test
        void shouldReturnEmptyList() throws Exception {
            mockMvc.perform(get("/api/reservations")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("DELETE /api/reservations/{id}")
    class DeleteReservation {

        @Test
        void shouldDeleteReservation() throws Exception {
            Reservation reservation = reservationRepository.save(
                    Reservation.builder()
                            .reservationDate(LocalDate.now().plusDays(2))
                            .timeSlot(TimeSlot.AFTERNOON)
                            .status(Status.ACTIVE)
                            .emailSent(false)
                            .createdAt(LocalDateTime.now())
                            .user(testUser)
                            .space(testSpace)
                            .build()
            );

            mockMvc.perform(delete("/api/reservations/{id}", reservation.getId())
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Reservation deleted successfully"));
        }

        @Test
        void shouldReturn404IfReservationDoesNotExist() throws Exception {
            mockMvc.perform(delete("/api/reservations/99999")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/reservations")
    class AddReservation {

        @Test
        void shouldCreateReservation() throws Exception {
            ReservationRequest request = new ReservationRequest(
                    LocalDate.now().plusDays(1),
                    TimeSlot.MORNING,
                    testSpace.getId()
            );

            mockMvc.perform(post("/api/reservations")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }
    }
}