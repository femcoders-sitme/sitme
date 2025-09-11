package com.femcoders.sitme.space;

import jakarta.persistence.*;

@Entity
@Table(name = "spaces")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
