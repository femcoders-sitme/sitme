package com.femcoders.sitme.reservation.services;

import java.util.List;

import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;

public interface ReservationService {
    List<ReservationResponse> getAllReservations();
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getMyReservations(CustomUserDetails userDetails);
	ReservationResponse addReservation(ReservationRequest request, CustomUserDetails userDetails);
	boolean isReservationAvailable(ReservationRequest reservationRequest);
}
