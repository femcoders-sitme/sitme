package com.femcoders.sitme.reservation.dtos;

import com.femcoders.sitme.reservation.TimeSlot;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequest(

        @NotNull(message = "Reservation date is required")
        @FutureOrPresent(message = "Reservation date cannot be in the past")
        LocalDate reservationDate,

        @NotNull(message = "Time slot is required")
        TimeSlot timeSlot,

        @NotNull(message = "Space id is required")
        Long spaceId
) {
}
