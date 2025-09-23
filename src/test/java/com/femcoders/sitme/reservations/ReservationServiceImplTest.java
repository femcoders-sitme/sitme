package com.femcoders.sitme.reservations;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.reservation.services.ReservationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
@DisplayName("Reservation Service Tests")
public class ReservationServiceImplTest {

    private static final Long TEST_RESERVATION_ID_1 = 1L;
    private static final Long TEST_RESERVATION_ID_2 = 2L;
    private static final Long TEST_NON_EXISTENT_ID = 999L;

    private static final String TEST_CUSTOMER_NAME_1 = "Ana García";
    private static final String TEST_CUSTOMER_NAME_2 = "María López";

    private static final Integer TEST_TABLE_NUMBER_1 = 5;
    private static final Integer TEST_TABLE_NUMBER_2 = 3;

    private static final Integer TEST_ROOM_SIZE_1 = 4;
    private static final Integer TEST_ROOM_SIZE_2 = 2;

    private static final LocalDateTime TEST_RESERVATION_TIME_1 =
            LocalDateTime.of(2024, 12, 25, 19, 30);
    private static final LocalDateTime TEST_RESERVATION_TIME_2 =
            LocalDateTime.of(2024, 12, 26, 20, 0);

    private static final String ERROR_DATABASE = "Database error";
    private static final String ERROR_DATABASE_CONNECTION = "Database connection failed";
    private static final String ERROR_RESERVATION_NOT_FOUND = "Reservation not found with id ";

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";


    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;


}
