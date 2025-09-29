package com.femcoders.sitme.shared.exceptions;

public enum ErrorCode {

    NOT_FOUND, // Entity not found

    AUTH_01, // Invalid credentials
    AUTH_02, // Email or username already registered
    AUTH_03, // Invalid token
    AUTH_04, // Expired token

    VALIDATION_ERROR, //Validation error

    SPACE_01, // Space already exists

    RESERVATION_01, // Invalid reservation request
    RESERVATION_02, // Reservation request not found
    RESERVATION_03, // Unauthorized reservation request access

    SERVER_01 // Internal server error
}
