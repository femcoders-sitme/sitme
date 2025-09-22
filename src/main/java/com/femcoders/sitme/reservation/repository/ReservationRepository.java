package com.femcoders.sitme.reservation.repository;

import com.femcoders.sitme.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
