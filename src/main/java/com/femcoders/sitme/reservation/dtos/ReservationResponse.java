package com.femcoders.sitme.reservation.dtos;

import com.femcoders.sitme.reservation.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        LocalDate reservationDate,
        TimeSlot timeSlot,
        boolean emailSent,
        LocalDateTime createdAt,

        Long userId,
        String username,

        Long spaceId,
        String spaceName
) {
}
