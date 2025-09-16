package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.dtos.ReservationMapper;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id " + id));

        return ReservationMapper.entityToDto(reservation);
    }
}
