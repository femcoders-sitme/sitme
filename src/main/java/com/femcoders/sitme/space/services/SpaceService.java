package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceRecordResponse;
import java.util.List;

public interface SpaceService {
    List<SpaceRecordResponse> getAllSpaces();
    List<SpaceRecordResponse> getSpacesByType(SpaceType type);
    List<SpaceRecordResponse> getAvailableSpaces();
}