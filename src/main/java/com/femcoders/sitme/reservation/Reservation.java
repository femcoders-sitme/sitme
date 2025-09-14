package com.femcoders.sitme.reservation;

import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "space_id")
    private Space space;
}
