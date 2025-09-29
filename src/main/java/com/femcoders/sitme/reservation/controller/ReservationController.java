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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SuccessResponse<List<ReservationResponse>>> getMyReservations(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ReservationResponse> reservations = reservationService.getMyReservations(userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Reservations list retrieved successfully", reservations));
    }
    
    //TODO Add Swagger description
    //TODO Fix the response in case of conflict. It should be failed
    @PostMapping
    public  ResponseEntity<SuccessResponse<ReservationResponse>> createReservation(@RequestBody @Valid ReservationRequest reservationRequest,
    		@AuthenticationPrincipal CustomUserDetails userDetails) {
    	if(!reservationService.isReservationAvailable(reservationRequest)) {
    		return ResponseEntity.status(HttpStatus.CONFLICT).body(SuccessResponse.of("Space is already booked for this date and time slot", null));
    	}
    	ReservationResponse response = reservationService.addReservation(reservationRequest, userDetails);
    	return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Reservation created successfully", response));
    	
    }
}

