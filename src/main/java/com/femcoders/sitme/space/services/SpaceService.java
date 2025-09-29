package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.dto.SpaceResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpaceService {
    List<SpaceResponse> getAllSpaces();
    List<SpaceResponse> getSpacesByType(SpaceType type);
    SpaceResponse addSpace(SpaceRequest spaceRequest, MultipartFile file);
    SpaceResponse updateSpace(Long id, SpaceRequest spaceRequest, MultipartFile file);
    void deleteSpace(Long id);
}