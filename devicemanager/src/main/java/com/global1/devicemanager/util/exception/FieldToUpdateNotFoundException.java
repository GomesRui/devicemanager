package com.global1.devicemanager.util.exception;

public class FieldToUpdateNotFoundException extends RuntimeException {
    public FieldToUpdateNotFoundException(String field, Long id) {
        super("Field not found \"" + field + "\" for the device id " + id);
    }
}
