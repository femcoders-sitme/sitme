package com.femcoders.sitme.space.exceptions;

import com.femcoders.sitme.shared.exceptions.ErrorCode;

public class SpaceAlreadyExistsException extends RuntimeException {
    public SpaceAlreadyExistsException(String name) {
        super("Space with name '" + name + "' already exists");
    }

    public ErrorCode getErrorCode() {
        return ErrorCode.SPACE_02;
    }
}

