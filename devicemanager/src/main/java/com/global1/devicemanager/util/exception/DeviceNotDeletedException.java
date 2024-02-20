package com.global1.devicemanager.util.exception;

public class DeviceNotDeletedException extends RuntimeException {
    public DeviceNotDeletedException(Long id) {
        super("Device not deleted for the id " + id);
    }
}
