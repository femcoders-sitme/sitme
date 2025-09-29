package com.femcoders.sitme.space.dto;

import com.femcoders.sitme.space.Space;

public class SpaceMapper {
        public static Space dtoToEntity(SpaceRequest request) {
        if (request == null) return null;

        return Space.builder()
                .name(request.name())
                .capacity(request.capacity())
                .type(request.type())
                .imageUrl(request.imageUrl())
                .build();
    }

    public static SpaceResponse entityToDto(Space space) {
        if (space == null) return null;

        return new SpaceResponse(
                space.getName(),
                space.getCapacity(),
                space.getType().name(),
                space.getImageUrl()
        );
    }
}