package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.SpaceRepository;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceRecordMapperImpl;
import com.femcoders.sitme.space.dto.SpaceRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceRecordMapperImpl mapper = new SpaceRecordMapperImpl();

    @Override
    public List<SpaceRecordResponse> getAllSpaces() {
        return spaceRepository.findAll()
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }
    @Override
    public List<SpaceRecordResponse> getSpacesByType(SpaceType type) {
        return spaceRepository.findByType(type)
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<SpaceRecordResponse> getAvailableSpaces() {
        return spaceRepository.findByIsAvailableTrue()
                .stream()
                .map(mapper::entityToDto)
                .toList();
    }
}