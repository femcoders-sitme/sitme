package com.femcoders.sitme.shared.exceptions;

import com.femcoders.sitme.user.exceptions.InvalidCredentialsException;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
