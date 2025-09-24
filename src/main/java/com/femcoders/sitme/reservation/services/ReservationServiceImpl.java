package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.dtos.ReservationMapper;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationsRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<ReservationResponse> getAllReservations() {

        return reservationsRepository.findAll()
                .stream()
                .map(ReservationMapper::entityToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Reservation.class.getSimpleName(), id));

        return ReservationMapper.entityToDto(reservation);
    }

    @Override
    public List<ReservationResponse> getMyReservations(CustomUserDetails userDetails) {

        return reservationsRepository.findByUserId(userDetails.getId())
                .stream()
                .map(ReservationMapper::entityToDto)
                .toList();
    }
}
