package com.femcoders.sitme.reservation.dtos;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.user.User;

public class ReservationMapper {

    public static Reservation dtoToEntity(ReservationRequest request, User user, Space space) {
        return Reservation.builder()
                .reservationDate(request.reservationDate())
                .timeSlot(request.timeSlot())
                .emailSent(false)
                .user(user)
                .space(space)
                .build();
    }

    public static ReservationResponse entityToDto(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationDate(),
                reservation.getTimeSlot(),
                reservation.isEmailSent(),
                reservation.getCreatedAt(),
                reservation.getUser().getId(),
                reservation.getUser().getUsername(),
                reservation.getSpace().getId(),
                reservation.getSpace().getName()
        );
    }
}
