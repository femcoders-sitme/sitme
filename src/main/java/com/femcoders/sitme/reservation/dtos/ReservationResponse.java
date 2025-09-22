package com.femcoders.sitme.reservation.dtos;

import com.femcoders.sitme.reservation.Status;
import com.femcoders.sitme.reservation.TimeSlot;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        LocalDate reservationDate,
        TimeSlot timeSlot,
        Status status,
        boolean emailSent,
        LocalDateTime createdAt,

        Long userId,
        String username,

        Long spaceId,
        String spaceName
) {
}
