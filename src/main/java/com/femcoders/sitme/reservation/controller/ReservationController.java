package com.femcoders.sitme.reservation.controller;

import com.femcoders.sitme.reservation.dtos.ReservationResponse;
import com.femcoders.sitme.reservation.services.ReservationServiceImpl;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))))
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<List<ReservationResponse>>> getAllReservations() {

        List<ReservationResponse> reservations = reservationService.getAllReservations();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Reservations list retrieved successfully", reservations));
    }

    @Operation(summary = "Get reservation by ID",
            description = "Retrieve a specific reservation by its identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @Parameter(description = "Reservation's id", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ReservationResponse>> getReservationById(@PathVariable Long id) {

        ReservationResponse reservation = reservationService.getReservationById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Reservation retrieved successfully", reservation));
    }
}

