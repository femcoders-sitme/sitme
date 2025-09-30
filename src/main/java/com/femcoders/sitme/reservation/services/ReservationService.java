package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;

import java.util.List;

public interface ReservationService {
    List<ReservationResponse> getAllReservations();
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getMyReservations(CustomUserDetails userDetails);

    void deleteReservation(Long id);

    ReservationResponse createReservation(ReservationRequest reservationRequest, CustomUserDetails userDetails);

}
