package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.user.Role;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        String username,
        String email,
        Role role,
        LocalDateTime createdAt,
        List<Reservation> reservations
) {
}
