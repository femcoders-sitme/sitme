package com.femcoders.sitme.space.dto;

public record SpaceRecordResponse(
        Long id,
        String name,
        String location,
        Integer capacity,
        String type,
        Boolean available,
        String imageUrl
) {
}

