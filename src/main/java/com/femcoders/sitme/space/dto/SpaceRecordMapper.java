package com.femcoders.sitme.space.dto;

import com.femcoders.sitme.space.Space;

public interface SpaceRecordMapper {
Space dtoToEntity (SpaceRecordRequest request);
    SpaceRecordResponse entityToDto (Space space);
}
