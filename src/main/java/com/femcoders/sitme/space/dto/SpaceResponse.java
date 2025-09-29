package com.femcoders.sitme.space.dto;

public record SpaceResponse(
        String name,
        Integer capacity,
        String type,
        String imageUrl
) {
}

