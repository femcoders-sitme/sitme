package com.femcoders.sitme.space;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.space.dto.SpaceMapper;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.dto.SpaceResponse;
import com.femcoders.sitme.space.exceptions.InvalidSpaceNameException;
import com.femcoders.sitme.space.exceptions.SpaceAlreadyExistsException;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.space.services.SpaceServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpaceService Unit Tests")
public class SpaceServiceImplTest {

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private SpaceServiceImpl spaceService;

    @Mock
    private CloudinaryService cloudinaryService;

    private Space space1;
    private Space space2;
    private SpaceResponse response1;
    private SpaceResponse response2;
    private SpaceResponse updateResponse1;
    private SpaceRequest request1;
    private SpaceRequest request2;
    private SpaceRequest updateRequest1;

    @BeforeEach
    void setUp() {
        space1 = Space.builder()
                .id(1L)
                .name("R-001")
                .capacity(8)
                .type(SpaceType.ROOM)
                .imageUrl("https://picsum.photos/seed/roomA/600/400")
                .build();

        space2 = Space.builder()
                .id(2L)
                .name("T-001")
                .capacity(2)
                .type(SpaceType.TABLE)
                .imageUrl("https://picsum.photos/seed/table01/600/400")
                .build();

        response1 = new SpaceResponse("R-001", 8, "ROOM",  "https://picsum.photos/seed/roomA/600/400");
        response2 = new SpaceResponse("T-001", 2, "TABLE", "https://picsum.photos/seed/table01/600/400");
        updateResponse1 = new SpaceResponse("R-001", 1, "ROOM",  "https://picsum.photos/seed/roomA/600/400");
        request1 = new SpaceRequest("R-001", 8, SpaceType.ROOM, "https://picsum.photos/seed/roomA/600/400");
        updateRequest1 = new SpaceRequest("R-001", 1, SpaceType.ROOM, "https://picsum.photos/seed/roomA/600/400");
        request2 = new SpaceRequest("", 2, SpaceType.TABLE, "https://picsum.photos/seed/table01/600/400");
    }

    @Nested
    @DisplayName("getAllSpaces()")
    class GetAllSpacesTests {

        @Test
        @DisplayName("Should return a list of all space responses")
        void shouldReturnListOfAllSpaceResponses() {

            given(spaceRepository.findAll()).willReturn(List.of(space1, space2));

            try (MockedStatic<SpaceMapper> mockedMapper = org.mockito.Mockito.mockStatic(SpaceMapper.class)) {
                mockedMapper.when(() -> SpaceMapper.entityToDto(space1)).thenReturn(response1);
                mockedMapper.when(() -> SpaceMapper.entityToDto(space2)).thenReturn(response2);

                List<SpaceResponse> result = spaceService.getAllSpaces();

                assertThat(result).hasSize(2);
                assertThat(result).containsExactly(response1, response2);

                verify(spaceRepository).findAll();
                mockedMapper.verify(() -> SpaceMapper.entityToDto(space1));
                mockedMapper.verify(() -> SpaceMapper.entityToDto(space2));
            }
        }
        @Test
        @DisplayName("Should return empty list when no spaces found")
        void shouldReturnEmptyListWhenNoSpacesFound() {
            given(spaceRepository.findAll()).willReturn(List.of());

            List<SpaceResponse> result = spaceService.getAllSpaces();

            assertThat(result).isEmpty();
            verify(spaceRepository).findAll();
        }
    }

    @Nested
    @DisplayName("addSpace()")
    class AddSpaceTests {

        @Test
        void addSpaceTest_ShouldAddNewSpaceSuccessfully() {

            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);

            when(spaceRepository.existsByName(request1.name())).thenReturn(false);
            when(spaceRepository.save(any(Space.class))).thenReturn(space1);

            try (MockedStatic<SpaceMapper> mockedMapper = Mockito.mockStatic(SpaceMapper.class)) {
                mockedMapper.when(() -> SpaceMapper.dtoToEntity(request1)).thenReturn(space1);
                mockedMapper.when(() -> SpaceMapper.entityToDto(space1)).thenReturn(response1);

                SpaceResponse actualResponse = spaceService.addSpace(request1, file);

                assertNotNull(actualResponse);
                assertEquals(response1, actualResponse);

                verify(spaceRepository).existsByName(request1.name());
                verify(spaceRepository).save(space1);
                verify(cloudinaryService).uploadEntityImage(space1, file, "sitme/spaces");
            }
        }

        @Test
        void addSpaceTest_ShouldThrowInvalidSpaceNameException_WhenNameIsBlank() {

            assertThrows(InvalidSpaceNameException.class, () -> {
                spaceService.addSpace(request2, null);
            });

            verify(spaceRepository, never()).save(any());
            verify(cloudinaryService, never()).uploadEntityImage(any(), any(), any());
        }

        @Test
        void addSpaceTest_ShouldThrowSpaceAlreadyExistsException_WhenNameExists() {

            when(spaceRepository.existsByName(request1.name())).thenReturn(true);

            assertThrows(SpaceAlreadyExistsException.class, () -> {
                spaceService.addSpace(request1, null);
            });

            verify(spaceRepository).existsByName(request1.name());
            verify(spaceRepository, never()).save(any());
            verify(cloudinaryService, never()).uploadEntityImage(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("deleteSpace()")
    class DeleteSpaceTests {

        @Test
        @DisplayName("Should delete space by ID when it exists")
        void shouldDeleteSpaceByIdWhenExists() {
            Long id = 1L;
            Space space = new Space();
            space.setId(id);

            given(spaceRepository.findById(id)).willReturn(Optional.of(space));

            spaceService.deleteSpace(id);

            verify(spaceRepository).findById(id);
            verify(spaceRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when space does not exist")
        void shouldThrowExceptionWhenSpaceDoesNotExist() {
            Long id = 99L;
            given(spaceRepository.findById(id)).willReturn(Optional.empty());

            Assertions.assertThrows(
                    RuntimeException.class,
                    () -> spaceService.deleteSpace(id),
                    "Space with ID " + id + " does not exist"
            );

            verify(spaceRepository).findById(id);
        }
    }
}
