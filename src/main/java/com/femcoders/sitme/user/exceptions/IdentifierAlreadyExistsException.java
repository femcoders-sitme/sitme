package com.femcoders.sitme.user.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class IdentifierAlreadyExistsException extends RuntimeException {
    public IdentifierAlreadyExistsException(String message) {
        super(message);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.AUTH_02;
    }
}
