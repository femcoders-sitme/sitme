package com.femcoders.sitme.space;

import com.femcoders.sitme.space.dto.SpaceMapper;
import com.femcoders.sitme.space.dto.SpaceResponse;
import com.femcoders.sitme.space.repository.SpaceRepository;
import com.femcoders.sitme.space.services.SpaceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpaceService Unit Tests")
public class SpaceServiceImplTest {

    @Mock
    private SpaceRepository spaceRepository;

    @InjectMocks
    private SpaceServiceImpl spaceService;

    private Space space1;
    private Space space2;
    private SpaceResponse response1;
    private SpaceResponse response2;

    @BeforeEach
    void setUp() {
        space1 = Space.builder()
                .id(1L)
                .name("R-001")
                .capacity(8)
                .type(SpaceType.ROOM)
                .isAvailable(true)
                .imageUrl("https://picsum.photos/seed/roomA/600/400")
                .build();

        space2 = Space.builder()
                .id(2L)
                .name("T-001")
                .capacity(2)
                .type(SpaceType.TABLE)
                .isAvailable(true)
                .imageUrl("https://picsum.photos/seed/table01/600/400")
                .build();

        response1 = new SpaceResponse("R-001", 8, "ROOM", true, "https://picsum.photos/seed/roomA/600/400");
        response2 = new SpaceResponse("T-001", 2, "TABLE", true, "https://picsum.photos/seed/table01/600/400");
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
    @DisplayName("deleteSpace()")
    class DeleteSpaceTests {

        @Test
        @DisplayName("Should delete space by ID when it exists")
        void shouldDeleteSpaceByIdWhenExists() {
            Long id = 1L;
            given(spaceRepository.existsById(id)).willReturn(true);

            spaceService.deleteSpace(id);

            verify(spaceRepository).existsById(id);
            verify(spaceRepository).deleteById(id);
        }

        @Test
        @DisplayName("Should throw exception when space does not exist")
        void shouldThrowExceptionWhenSpaceDoesNotExist() {
            Long id = 99L;
            given(spaceRepository.existsById(id)).willReturn(false);

            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> spaceService.deleteSpace(id),
                    "Space with ID " + id + " does not exist"
            );

            verify(spaceRepository).existsById(id);
        }
    }
}
