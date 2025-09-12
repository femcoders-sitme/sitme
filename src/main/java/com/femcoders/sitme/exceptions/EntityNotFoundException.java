package com.femcoders.sitme.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " not found with id " + id);
    }
    public EntityNotFoundException(String entityName, String value) {
        super(entityName + " not found with " + value);
    }
}