package com.femcoders.sitme.reservation.services;

import com.femcoders.sitme.email.EmailService;
import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.dtos.ReservationMapper;
import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.repository.ReservationRepository;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final EmailService emailService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteReservation(Long id) {
        Reservation reservation = reservationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Reservation.class.getSimpleName(), id));

        reservationsRepository.delete(reservation);
     } 

    @Override
    public ReservationResponse createReservation(ReservationRequest reservationRequest, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(()->new EntityNotFoundException(User.class.getSimpleName(), userDetails.getId()));

        Space space = spaceRepository.findById(reservationRequest.spaceId())
                .orElseThrow(()->new EntityNotFoundException(Space.class.getSimpleName(), reservationRequest.spaceId()));

        Reservation reservationNew = ReservationMapper.dtoToEntity(reservationRequest, user, space);
        Reservation reservationSaved = reservationsRepository.save(reservationNew);
        emailService.sendReservationConfirmationEmail(
                reservationSaved.getUser().getEmail(),
                reservationSaved.getUser().getUsername(),
                reservationSaved.getSpace().getName(),
                reservationSaved.getReservationDate().toString(),
                reservationSaved.getTimeSlot().name());

        return ReservationMapper.entityToDto(reservationSaved);
    }
}
