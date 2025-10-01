package com.femcoders.sitme.shared.exceptions;

import com.femcoders.sitme.user.exceptions.IdentifierAlreadyExistsException;
import com.femcoders.sitme.space.exceptions.InvalidSpaceNameException;
import com.femcoders.sitme.space.exceptions.SpaceAlreadyExistsException;
import com.femcoders.sitme.user.exceptions.InvalidCredentialsException;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(ErrorCode errorCode, String message, HttpStatus httpStatus, String path) {
        return new ErrorResponse(errorCode, message, httpStatus, path);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNameNotFoundException(UserNameNotFoundException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                ErrorCode.AUTH_05,
                exception.getMessage(),
                HttpStatus.FORBIDDEN,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                ErrorCode.AUTH_06,
                "Authentication failed: invalid credentials or token",
                HttpStatus.UNAUTHORIZED,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IdentifierAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleIdentifierAlreadyExists(IdentifierAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                ErrorCode.RESERVATION_01,
                exception.getMessage(),
                HttpStatus.CONFLICT,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Invalid input");

        ErrorResponse error = buildErrorResponse(
                ErrorCode.VALIDATION_ERROR,
                message,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                ErrorCode.SERVER_01,
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SpaceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSpaceAlreadyExists(SpaceAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidSpaceNameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSpaceName(InvalidSpaceNameException exception, HttpServletRequest request) {
        ErrorResponse errorResponse = buildErrorResponse(
                exception.getErrorCode(),
                exception.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
