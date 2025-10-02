package com.femcoders.sitme.space.dto;

public record SpaceResponse(
        Long id,
        String name,
        Integer capacity,
        String type,
        String imageUrl
) {
}

