package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReservationService {
    List<ReservationResponse> getAllReservations();
    ReservationResponse getReservationById(Long id);
    List<ReservationResponse> getMyReservations(CustomUserDetails userDetails);
}
