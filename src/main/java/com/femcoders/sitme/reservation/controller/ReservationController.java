package com.femcoders.sitme.reservation.controller;

import com.femcoders.sitme.reservation.dtos.ReservationRequest;
import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.services.ReservationServiceImpl;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Endpoints for managing reservations")
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @Operation(
            summary = "Get all reservations",
            description = "Returns a list of all registered reservations."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ReservationResponse>>> getAllReservations() {

        List<ReservationResponse> reservations = reservationService.getAllReservations();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Reservations list retrieved successfully", reservations));
    }

    @Operation(
            summary = "Get reservation by ID",
            description = "Retrieve a specific reservation by its identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ReservationResponse>> getReservationById(
            @Parameter(description = "Reservation's id", required = true)
            @PathVariable Long id) {

        ReservationResponse reservation = reservationService.getReservationById(id);

        return ResponseEntity.ok(SuccessResponse.of("Reservation retrieved successfully", reservation));
    }

    @Operation(
            summary = "Get my reservations",
            description = "Returns the list of reservations associated with the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservations retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<List<ReservationResponse>>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ReservationResponse> reservations = reservationService.getMyReservations(userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Reservations list retrieved successfully", reservations));
    }

    @Operation(
            summary = "Delete reservation",
            description = "Deletes a reservation by its ID. Only admins can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reservation deleted successfully",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<String>> deleteReservation(
            @Parameter(description = "Reservation ID to delete", required = true)
            @PathVariable Long id) {

        reservationService.deleteReservation(id); 
        return ResponseEntity.ok(SuccessResponse.of("Reservation deleted successfully", null));
     }

    @Operation(
            summary = "Create a reservation",
            description = "Creates a new reservation associated with the authenticated user. " +
                    "The reservation will be stored with status ACTIVE by default and emailSent set to false."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation created successfully",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error (e.g., past date or missing required fields)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthenticated - missing or invalid JWT"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User or Space not found"
            )
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<ReservationResponse>> createReservation (
            @Valid @RequestBody ReservationRequest reservationRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!reservationService.isReservationAvailable(reservationRequest)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(SuccessResponse.of("This space is already booked for this date and time slot", null));
        }

        ReservationResponse reservationNew = reservationService.createReservation(reservationRequest, userDetails);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Reservation created successfully", reservationNew));
    }
}

