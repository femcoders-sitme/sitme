package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.dto.SpaceResponse;
import java.util.List;

public interface SpaceService {
    List<SpaceResponse> getAllSpaces();
    List<SpaceResponse> getSpacesByType(SpaceType type);
    List<SpaceResponse> getAvailableSpaces();
    SpaceResponse addSpace(SpaceRequest spaceRequest);

}