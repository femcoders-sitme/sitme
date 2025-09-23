package com.femcoders.sitme.reservations;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.Status;
import com.femcoders.sitme.reservation.TimeSlot;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.reservation.services.ReservationServiceImpl;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Reservation Service Tests")
public class ReservationServiceImplTest {

    private static final Long TEST_RESERVATION_ID = 1L;
    private static final Long TEST_ANOTHER_RESERVATION_ID = 2L;
    private static final Long TEST_NON_EXISTENT_ID = 999L;

    private static final Long TEST_USER_ID = 100L;
    private static final Long TEST_ANOTHER_USER_ID = 200L;
    private static final String TEST_USER_NAME = "Ana García";
    private static final String TEST_ANOTHER_USER_NAME = "María López";
    private static final String TEST_USER_EMAIL = "ana.garcia@test.com";
    private static final String TEST_ANOTHER_USER_EMAIL = "maria.lopez@test.com";

    private static final Long TEST_SPACE_ID = 10L;
    private static final Long TEST_ANOTHER_SPACE_ID = 20L;
    private static final String TEST_SPACE_NAME = "Conference Room A";
    private static final String TEST_ANOTHER_SPACE_NAME = "Meeting Room B";

    private static final LocalDate TEST_RESERVATION_DATE = LocalDate.of(2024, 12, 25);
    private static final LocalDate TEST_ANOTHER_RESERVATION_DATE = LocalDate.of(2024, 12, 26);
    private static final TimeSlot TEST_TIME_SLOT = TimeSlot.MORNING;
    private static final TimeSlot TEST_ANOTHER_TIME_SLOT = TimeSlot.AFTERNOON;
    private static final Status TEST_STATUS = Status.ACTIVE;
    private static final Status TEST_ANOTHER_STATUS = Status.COMPLETED;
    private static final boolean TEST_EMAIL_SENT = true;
    private static final boolean TEST_ANOTHER_EMAIL_SENT = false;

    private static final LocalDateTime TEST_CREATED_AT = LocalDateTime.of(2024, 12, 20, 10, 30);
    private static final LocalDateTime TEST_ANOTHER_CREATED_AT = LocalDateTime.of(2024, 12, 21, 14, 45);

    private static final String ERROR_DATABASE = "Database error";
    private static final String ERROR_DATABASE_CONNECTION = "Database connection failed";
    private static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found with id ";

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation testReservation;
    private Reservation secondTestReservation;
    private User testUser;
    private User secondTestUser;
    private Space testSpace;
    private Space secondTestSpace;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        secondTestUser = createSecondTestUser();
        testSpace = createTestSpace();
        secondTestSpace = createSecondTestSpace();
        testReservation = createTestReservation();
        secondTestReservation = createSecondTestReservation();
    }

    @Nested
    @DisplayName("GET /reservations")
    class GetAllReservationsTests {

        @Test
        @DisplayName("Should return reservations list when data exists")
        void shouldReturnReservationListWhenDataExists() {

            List<Reservation> testReservations = List.of(testReservation, secondTestReservation);

            when(reservationRepository.findAll()).thenReturn(testReservations);

            List<ReservationResponse> result = reservationService.getAllReservations();

            assertNotNull(result);
            assertEquals(2, result.size());

            assertEquals(testReservation.getReservationDate(), result.getFirst().reservationDate());
            assertEquals(testReservation.getTimeSlot(), result.getFirst().timeSlot());
            assertEquals(testReservation.getStatus(), result.getFirst().status());
            assertEquals(testReservation.isEmailSent(), result.getFirst().emailSent());
            assertEquals(testReservation.getCreatedAt(), result.getFirst().createdAt());
            assertEquals(testReservation.getUser().getId(), result.getFirst().userId());
            assertEquals(testReservation.getUser().getUsername(), result.getFirst().username());
            assertEquals(testReservation.getSpace().getId(), result.get(0).spaceId());
            assertEquals(testReservation.getSpace().getName(), result.get(0).spaceName());

            assertEquals(secondTestReservation.getReservationDate(), result.get(1).reservationDate());
            assertEquals(secondTestReservation.getTimeSlot(), result.get(1).timeSlot());
            assertEquals(secondTestReservation.getStatus(), result.get(1).status());
            assertEquals(secondTestReservation.isEmailSent(), result.get(1).emailSent());
            assertEquals(secondTestReservation.getCreatedAt(), result.get(1).createdAt());
            assertEquals(secondTestReservation.getUser().getId(), result.get(1).userId());
            assertEquals(secondTestReservation.getUser().getUsername(), result.get(1).username());
            assertEquals(secondTestReservation.getSpace().getId(), result.get(1).spaceId());
            assertEquals(secondTestReservation.getSpace().getName(), result.get(1).spaceName());

            verify(reservationRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no reservations exist")
        void shouldReturnEmptyListWhenNoReservationsExist() {

            when(reservationRepository.findAll()).thenReturn(Collections.emptyList());

            List<ReservationResponse> result = reservationService.getAllReservations();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(reservationRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should propagate exception when repository fails")
        void shouldPropagateExceptionWhenRepositoryFails() {

            when(reservationRepository.findAll()).thenThrow(new RuntimeException(ERROR_DATABASE));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reservationService.getAllReservations());
            assertEquals(ERROR_DATABASE, exception.getMessage());
            verify(reservationRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("GET /reservations/{id}")
    class GetReservationByIdTests {

        @Test
        @DisplayName("Should return reservation when ID exists")
        void shouldReturnReservationWhenIdExists() {
            
            when(reservationRepository.findById(TEST_RESERVATION_ID)).thenReturn(Optional.of(testReservation));

            ReservationResponse result = reservationService.getReservationById(TEST_RESERVATION_ID);

            assertNotNull(result);
            assertEquals(testReservation.getReservationDate(), result.reservationDate());
            assertEquals(testReservation.getTimeSlot(), result.timeSlot());
            assertEquals(testReservation.getStatus(), result.status());
            assertEquals(testReservation.isEmailSent(), result.emailSent());
            assertEquals(testReservation.getCreatedAt(), result.createdAt());
            assertEquals(testReservation.getUser().getId(), result.userId());
            assertEquals(testReservation.getUser().getUsername(), result.username());
            assertEquals(testReservation.getSpace().getId(), result.spaceId());
            assertEquals(testReservation.getSpace().getName(), result.spaceName());

            verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when ID does not exist")
        void shouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {

            when(reservationRepository.findById(TEST_NON_EXISTENT_ID)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> reservationService.getReservationById(TEST_NON_EXISTENT_ID));
            assertEquals(ERROR_RESERVATION_NOT_FOUND + TEST_NON_EXISTENT_ID, exception.getMessage());
            verify(reservationRepository, times(1)).findById(TEST_NON_EXISTENT_ID);
        }

        @Test
        @DisplayName("Should propagate exception when repository fails")
        void shouldPropagateExceptionWhenRepositoryFails() {

            when(reservationRepository.findById(TEST_RESERVATION_ID))
                    .thenThrow(new RuntimeException(ERROR_DATABASE_CONNECTION));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> reservationService.getReservationById(TEST_RESERVATION_ID));
            assertEquals(ERROR_DATABASE_CONNECTION, exception.getMessage());
            verify(reservationRepository, times(1)).findById(TEST_RESERVATION_ID);
        }
    }

    private User createTestUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USER_NAME);
        user.setEmail(TEST_USER_EMAIL);
        return user;
    }

    private User createSecondTestUser(){
        User user = new User();
        user.setId(TEST_ANOTHER_USER_ID);
        user.setUsername(TEST_ANOTHER_USER_NAME);
        user.setEmail(TEST_ANOTHER_USER_EMAIL);
        return user;
    }

    private Space createTestSpace() {
        Space space = new Space();
        space.setId(TEST_SPACE_ID);
        space.setName(TEST_SPACE_NAME);
        return space;
    }

    private Space createSecondTestSpace() {
        Space space = new Space();
        space.setId(TEST_ANOTHER_SPACE_ID);
        space.setName(TEST_ANOTHER_SPACE_NAME);
        return space;
    }

    private Reservation createTestReservation() {
        return Reservation.builder()
                .id(TEST_RESERVATION_ID)
                .reservationDate(TEST_RESERVATION_DATE)
                .timeSlot(TEST_TIME_SLOT)
                .status(TEST_STATUS)
                .emailSent(TEST_EMAIL_SENT)
                .createdAt(TEST_CREATED_AT)
                .user(testUser)
                .space(testSpace)
                .build();
    }

    private Reservation createSecondTestReservation() {
        return Reservation.builder()
                .id(TEST_ANOTHER_RESERVATION_ID)
                .reservationDate(TEST_ANOTHER_RESERVATION_DATE)
                .timeSlot(TEST_ANOTHER_TIME_SLOT)
                .status(TEST_ANOTHER_STATUS)
                .emailSent(TEST_ANOTHER_EMAIL_SENT)
                .createdAt(TEST_ANOTHER_CREATED_AT)
                .user(secondTestUser)
                .space(secondTestSpace)
                .build();
    }
}
