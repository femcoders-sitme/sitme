package com.femcoders.sitme.shared.exceptions;

public enum ErrorCode {
    AUTH_01, // User not found
    AUTH_02, // Invalid credentials
    AUTH_03, // Email or username already registered
    AUTH_04, // Invalid token
    AUTH_05, // Expired token

    NOT_FOUND, // Entity not found

    VALIDATION_01, //Validation error

    SPACE_01, // Space not found
    SPACE_02, // Space already exists

    RESERVATION_01, // Invalid reservation request
    RESERVATION_02, // Reservation request not found
    RESERVATION_03, // Unauthorized reservation request access

    SERVER_01 // Internal server error
}
