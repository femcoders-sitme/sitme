package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceMapper;
import com.femcoders.sitme.space.dto.SpaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;

    @Override
    public List<SpaceResponse> getAllSpaces() {
        return spaceRepository.findAll()
                .stream()
                .map(SpaceMapper::entityToDto)
                .toList();
    }
    @Override
    public List<SpaceResponse> getSpacesByType(SpaceType type) {
        return spaceRepository.findByType(type)
                .stream()
                .map(SpaceMapper::entityToDto)
                .toList();
    }

    @Override
    public List<SpaceResponse> getAvailableSpaces() {
        return spaceRepository.findByIsAvailableTrue()
                .stream()
                .map(SpaceMapper::entityToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SpaceResponse addSpace(SpaceRequest spaceRequest) {

        boolean existsSpace = spaceRepository.existsByName(spaceRequest.name());

        if (existsSpace) {
            throw  new IllegalArgumentException("This space already exists");
        }

        Space newSpace = SpaceMapper.dtoToEntity(spaceRequest);
        Space savedSpace = spaceRepository.save(newSpace);

        return SpaceMapper.entityToDto(savedSpace);
    }
}