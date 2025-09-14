package com.femcoders.sitme.space;


import com.femcoders.sitme.space.dto.SpaceRecordRequest;
import com.femcoders.sitme.space.dto.SpaceRecordResponse;
import com.femcoders.sitme.space.services.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Spaces", description = "Endpoints for managing spaces")
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @Operation(
            summary = "Get all spaces",
            description = "Returns a list of all registered spaces."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spaces retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceRecordResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<SpaceRecordResponse>> getAllSpaces() {
        return ResponseEntity.ok(spaceService.getAllSpaces());
    }
    @Operation(
            summary = "Filter spaces by type",
            description = "Returns a list of spaces filtered by type (ROOM, TABLE)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spaces filtered successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceRecordResponse.class))))
    })
    @GetMapping("/filter/type")
    public ResponseEntity<List<SpaceRecordResponse>> getSpacesByType(@RequestParam SpaceType type) {
        return ResponseEntity.ok(spaceService.getSpacesByType(type));
    }

    @Operation(
            summary = "Filter available spaces",
            description = "Returns only spaces that are currently available."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available spaces retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceRecordResponse.class))))
    })
    @GetMapping("/filter/available")
    public ResponseEntity<List<SpaceRecordResponse>> getAvailableSpaces() {
        return ResponseEntity.ok(spaceService.getAvailableSpaces());
    }
}