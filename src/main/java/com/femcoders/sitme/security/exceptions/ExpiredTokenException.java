package com.femcoders.sitme.security.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_04;
    }
}
