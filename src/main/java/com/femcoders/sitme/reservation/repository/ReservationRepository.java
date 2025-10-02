package com.femcoders.sitme.reservation.repository;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.reservation.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByReservationDateAndSpaceIdAndStatus(
            LocalDate reservationDate,
            Long spaceId,
            Status status
    );
}
