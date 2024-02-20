package com.global1.devicemanager.util.exception;

public class FieldNotValidatedException extends RuntimeException {
    public FieldNotValidatedException(String field, String reason) {
        super("Field \"" + field + "\" failed validation: " + reason);
    }
}
