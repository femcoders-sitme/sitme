package com.femcoders.sitme.reservation.repository;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByReservationDateAndSpaceId(
            LocalDate reservationDate,
            Long spaceId);
}
