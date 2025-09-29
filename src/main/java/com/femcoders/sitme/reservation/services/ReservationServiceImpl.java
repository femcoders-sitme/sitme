package com.femcoders.sitme.reservation.services;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.dtos.ReservationMapper;
import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationsRepository;
    
    private final SpaceRepository spaceRepository;
    
    private final UserRepository userRepository;

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

	@Override
	public ReservationResponse addReservation(ReservationRequest request, CustomUserDetails userDetails) {
		Reservation reservation = ReservationMapper.dtoToEntity(request,
				userRepository.findById(userDetails.getId()).orElseThrow(),
				spaceRepository.findById(request.spaceId()).orElseThrow());
		return ReservationMapper.entityToDto(reservationsRepository.save(reservation));
	}

	@Override
	public boolean isReservationAvailable(ReservationRequest reservationRequest) {
		return reservationsRepository.findByReservationDateAndTimeSlot(reservationRequest.reservationDate(),
				reservationRequest.timeSlot()).isEmpty();
	}
}
