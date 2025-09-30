package com.femcoders.sitme.space.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class InvalidSpaceNameException extends RuntimeException {
    public InvalidSpaceNameException(String name) {
        super("Invalid space name: " + name);
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.VALIDATION_ERROR;
    }
}
