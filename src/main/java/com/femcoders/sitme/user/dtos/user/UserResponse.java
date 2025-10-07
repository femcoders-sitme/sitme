package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.user.Role;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        String imageUrl,
        String cloudinaryImageUrl
) {
}
