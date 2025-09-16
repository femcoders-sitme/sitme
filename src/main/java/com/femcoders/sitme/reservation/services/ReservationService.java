package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.reservation.dtos.ReservationResponse;

import java.util.List;

public interface ReservationService {
    List<ReservationResponse> getAllReservations();
    ReservationResponse getReservationById(Long id);
}
