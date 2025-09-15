package com.femcoders.sitme.space.dto;

import com.femcoders.sitme.space.Space;

public class SpaceMapper {
        public static Space dtoToEntity(SpaceRequest request) {
        if (request == null) return null;

        return Space.builder()
                .name(request.name())
                .location(request.location())
                .capacity(request.capacity())
                .type(request.type())
                .isAvailable(request.isAvailable() != null ? request.isAvailable() : true)
                .imageUrl(request.imageUrl())
                .build();
    }

    public static SpaceResponse entityToDto(Space space) {
        if (space == null) return null;

        return new SpaceResponse(
                space.getId(),
                space.getName(),
                space.getLocation().name().replace("_", " "),
                space.getCapacity(),
                space.getType().name(),
                space.getIsAvailable(),
                space.getImageUrl()
        );
    }
}