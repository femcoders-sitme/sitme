package com.femcoders.sitme.security.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_03;
    }
}
