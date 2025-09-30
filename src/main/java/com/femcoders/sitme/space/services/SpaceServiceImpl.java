package com.femcoders.sitme.space.services;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.space.Space;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.exceptions.InvalidSpaceNameException;
import com.femcoders.sitme.space.exceptions.SpaceAlreadyExistsException;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceMapper;
import com.femcoders.sitme.space.dto.SpaceResponse;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final CloudinaryService cloudinaryService;

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
    public SpaceResponse getSpaceById(Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException(Space.class.getSimpleName(), id));

        return SpaceMapper.entityToDto(space);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SpaceResponse addSpace(SpaceRequest spaceRequest, MultipartFile file) {

        if (spaceRequest.name() == null || spaceRequest.name().isBlank()) {
            throw new InvalidSpaceNameException(spaceRequest.name());
        }

        if (spaceRepository.existsByName(spaceRequest.name())) {
            throw new SpaceAlreadyExistsException(spaceRequest.name());
        }

        Space newSpace = SpaceMapper.dtoToEntity(spaceRequest);

        if (file != null && !file.isEmpty()) {
            cloudinaryService.uploadEntityImage(newSpace, file, "sitme/spaces");
        }

        Space savedSpace = spaceRepository.save(newSpace);

        return SpaceMapper.entityToDto(savedSpace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public SpaceResponse updateSpace(Long id, SpaceRequest spaceRequest, MultipartFile file){
        Space isExisting = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not exists by id: " + id));

        isExisting.setName(spaceRequest.name());
        isExisting.setCapacity(spaceRequest.capacity());
        isExisting.setType(spaceRequest.type());

        if (file != null && !file.isEmpty()) {
            cloudinaryService.deleteEntityImage(isExisting);
            cloudinaryService.uploadEntityImage(isExisting, file, "sitme/spaces");
        }

        Space savedSpace = spaceRepository.save(isExisting);
        return SpaceMapper.entityToDto(savedSpace);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteSpace(Long id) {
        Space spaceToDelete = spaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Space", id));
        if (spaceToDelete.getCloudinaryImageId() != null && !spaceToDelete.getCloudinaryImageId().isBlank()) {
            cloudinaryService.deleteEntityImage(spaceToDelete);
        }

        spaceRepository.deleteById(id);

    }
}
