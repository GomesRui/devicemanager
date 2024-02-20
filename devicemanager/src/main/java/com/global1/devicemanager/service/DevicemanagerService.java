package com.global1.devicemanager.service;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.repository.DevicemanagerRepository;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;
import com.global1.devicemanager.util.exception.FieldNotValidatedException;
import com.global1.devicemanager.util.exception.FieldToUpdateNotFoundException;

@Service
@Transactional
public class DevicemanagerService {

    @Autowired
    DevicemanagerRepository repo;

    public Device getDevice(Long id) throws Exception {
        return repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
    }

    public List<Device> getDevice(String brand, Errors errors) throws Exception {
        if (errors != null && errors.hasErrors())
            throw new FieldNotValidatedException(errors.getFieldError().getField(),
                    errors.getFieldError().getDefaultMessage());
        return repo.findByBrand(brand);
    }

    public List<Device> getDevices() {
        return repo.findAll();
    }

    public Device addDevice(Device device, Errors errors) throws Exception {
        if (errors != null && errors.hasErrors())
            throw new FieldNotValidatedException(errors.getFieldError().getField(),
                    errors.getFieldError().getDefaultMessage());
        return repo.save(device);
    }

    public Device updateDevice(Device device, Long id, Errors errors) throws Exception {
        if (errors != null && errors.hasErrors())
            throw new FieldNotValidatedException(errors.getFieldError().getField(),
                    errors.getFieldError().getDefaultMessage());

        Device deviceToUpdate = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        if (deviceToUpdate != null) {
            deviceToUpdate.setBrand(device.getBrand());
            deviceToUpdate.setName(device.getName());
        }

        return repo.save(deviceToUpdate);
    }

    public Device updateDevice(Map<String, Object> device, Long id, Errors errors) throws Exception {
        if (errors != null && errors.hasErrors())
            throw new FieldNotValidatedException(errors.getFieldError().getField(),
                    errors.getFieldError().getDefaultMessage());

        Device deviceToUpdate = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        if (deviceToUpdate != null) {
            device.forEach((k, v) -> {
                try {
                    PropertyDescriptor pd = new PropertyDescriptor(k, Device.class);
                    pd.getWriteMethod().invoke(deviceToUpdate, v);
                } catch (Exception ex) {
                    throw new FieldToUpdateNotFoundException(k, id);
                }
            });
        }

        return repo.save(deviceToUpdate);
    }

    public void deleteDevice(Long id) throws Exception {
        repo.deleteById(id);
        getDevice(id);
    }

}
