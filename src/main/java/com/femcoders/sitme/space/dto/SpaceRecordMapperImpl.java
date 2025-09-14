package com.femcoders.sitme.space.dto;

import com.femcoders.sitme.space.Space;

public class SpaceRecordMapperImpl implements SpaceRecordMapper {
    @Override
    public Space dtoToEntity(SpaceRecordRequest request) {
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

    @Override
    public SpaceRecordResponse entityToDto(Space space) {
        if (space == null) return null;

        return new SpaceRecordResponse(
                space.getId(),
                space.getName(),
                space.getLocation().name().replace("_", " "),
                space.getCapacity(),
                capitalize(space.getType().name()),
                space.getIsAvailable(),
                space.getImageUrl()
        );
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}