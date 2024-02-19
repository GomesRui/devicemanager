package com.global1.devicemanager.util.exception;

public class DeviceNotFoundException extends RuntimeException{
    public DeviceNotFoundException(Long id) {
        super("Device not found for the id " + id);
    }
}
