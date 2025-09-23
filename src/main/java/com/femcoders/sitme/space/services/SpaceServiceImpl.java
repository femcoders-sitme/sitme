package com.femcoders.sitme.space.services;

import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.exceptions.InvalidSpaceNameException;
import com.femcoders.sitme.space.exceptions.SpaceAlreadyExistsException;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceMapper;
import com.femcoders.sitme.space.dto.SpaceResponse;
import jakarta.persistence.EntityNotFoundException;
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

        if (spaceRequest.name() == null || spaceRequest.name().isBlank()) {
            throw new InvalidSpaceNameException(spaceRequest.name());
        }

        if (spaceRepository.existsByName(spaceRequest.name())) {
            throw new SpaceAlreadyExistsException(spaceRequest.name());
        }

        Space newSpace = SpaceMapper.dtoToEntity(spaceRequest);
        Space savedSpace = spaceRepository.save(newSpace);

        return SpaceMapper.entityToDto(savedSpace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SpaceResponse updateSpace(Long id, SpaceRequest spaceRequest){
        Space isExisting = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not exists by id: " + id));
        isExisting.setName(spaceRequest.name());
        isExisting.setCapacity(spaceRequest.capacity());
        isExisting.setType(spaceRequest.type());
        isExisting.setIsAvailable(spaceRequest.isAvailable());
        isExisting.setImageUrl(spaceRequest.imageUrl());
        Space savedSpace = spaceRepository.save(isExisting);
        return SpaceMapper.entityToDto(savedSpace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteSpace(Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new EntityNotFoundException("Space with ID " + id + " does not exist");
    }
        spaceRepository.deleteById(id);
    }
}
