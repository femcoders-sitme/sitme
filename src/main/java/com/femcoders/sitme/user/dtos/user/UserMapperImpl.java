package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.reservation.dtos.ReservationMapper;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

  /*  @Override
    public User dtoToEntity(UserRequest userRequestDTO, Role role) {
        return User.builder()
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password(userRequestDTO.password())
                .role(role)
                .build();
    }

    @Override
    public UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getReservations()
        );
    }*/

    public User dtoToEntity(UserRequest userRequestDTO, Role role) {
        return User.builder()
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password(userRequestDTO.password())
                .role(role)
                .build();
    }

    @Override
    public UserResponse entityToDto(User user) {
        // método estático de ReservationMapper
        List<ReservationResponse> reservationDTOs = new ArrayList<>();

        if (user.getReservations() != null && !user.getReservations().isEmpty()) {
            reservationDTOs = user.getReservations().stream()
                    .map(reservation -> ReservationMapper.entityToDto(reservation)) // ← Método estático
                    .toList();
        }

        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                reservationDTOs  // ✅ Lista de DTOs, no entidades
        );
    }
}
