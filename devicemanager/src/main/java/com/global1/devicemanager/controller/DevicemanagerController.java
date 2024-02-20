package com.global1.devicemanager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;

import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.service.DevicemanagerService;
import com.global1.devicemanager.util.exception.DeviceNotDeletedException;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;
import com.global1.devicemanager.util.exception.FieldNotValidatedException;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/devicemanager")
public class DevicemanagerController {

    @Autowired
    DevicemanagerService service;

    // --- GET ---
    @GetMapping("/")
    public ResponseEntity<List<Device>> get() {

        return ResponseEntity.ok(service.getDevices());
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<?> get(@PathVariable("id") Long id) {

        ResponseEntity<?> response;
        try {
            Device device = service.getDevice(id);
            response = ResponseEntity.ok(device);
        } catch (DeviceNotFoundException ex) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

        return response;
    }

    @GetMapping("/search")
    public ResponseEntity<?> get(@ModelAttribute("brand") @Valid String brand, Errors errors) {

        ResponseEntity<?> response;
        try {
            response = ResponseEntity.ok(service.getDevice(brand, errors));
        } catch (FieldNotValidatedException ex) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    // --- POST ---
    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody @Valid Device device, Errors errors) {

        ResponseEntity<?> response;
        try {
            response = ResponseEntity.ok(service.addDevice(device, errors));
        } catch (FieldNotValidatedException ex) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return response;
    }

    // --- PUT ---

    @PutMapping("/{id}")
    public ResponseEntity<?> fullUpdate(@PathVariable("id") Long id, @RequestBody @Valid Device device, Errors errors) {
        ResponseEntity<?> response;
        try {
            Device deviceUpdate = service.updateDevice(device, id, errors);
            response = ResponseEntity.ok(deviceUpdate);
        } catch (DeviceNotFoundException ex) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (FieldNotValidatedException ex) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdate(@PathVariable("id") Long id, @RequestBody @Valid Map<String, Object> device,
            Errors errors) {
        ResponseEntity<?> response;
        try {
            Device deviceUpdate = service.updateDevice(device, id, errors);
            response = ResponseEntity.ok(deviceUpdate);
        } catch (DeviceNotFoundException ex) {
            response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (FieldNotValidatedException ex) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        ResponseEntity<?> response = ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DeviceNotFoundException(id).getMessage());
        try {
            service.deleteDevice(id);
        } catch (DeviceNotFoundException ex) {
            response = ResponseEntity.ok("Device was deleted for id " + id);
        } catch (DeviceNotDeletedException ex) {
            response = ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ex.getMessage());
        } catch (Exception ex) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

        return response;
    }
}
