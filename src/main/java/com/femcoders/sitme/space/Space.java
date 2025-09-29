package com.femcoders.sitme.space;

import com.femcoders.sitme.reservation.Reservation;
import com.femcoders.sitme.shared.model.ImageUpdatable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "spaces")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Space implements ImageUpdatable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpaceType type;

    @Column(name = "image_url", length = 500)
    private String imageUrl;
    private String cloudinaryImageId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations;

}
