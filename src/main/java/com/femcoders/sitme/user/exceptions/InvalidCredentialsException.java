package com.femcoders.sitme.user.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_01;
    }
}
