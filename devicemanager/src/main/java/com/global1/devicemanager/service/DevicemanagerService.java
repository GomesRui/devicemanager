package com.global1.devicemanager.service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.el.util.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.repository.DevicemanagerRepository;
import com.global1.devicemanager.util.exception.DeviceNotDeletedException;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;
import com.global1.devicemanager.util.exception.FieldToUpdateNotFoundException;

@Service
@Transactional
public class DevicemanagerService {
    
    @Autowired
    DevicemanagerRepository repo;

    public Device getDevice(Long id) throws Exception {
        return repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
    }

    public List<Device> getDevice(String brand) {
        return repo.findByBrand(brand);
    }

    public List<Device> getDevices() {
        return repo.findAll();
    }

    public Device addDevice(Device device) {
        return repo.save(device);
    }

    public Device updateDevice(Device device, Long id) throws Exception{
        Device deviceToUpdate = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        if (deviceToUpdate != null) {
            deviceToUpdate.setBrand(device.getBrand());
            deviceToUpdate.setName(device.getName());
        }

        return repo.save(deviceToUpdate);
    }

    public Device updateDevice(Map<String,Object> device, Long id) throws Exception{
        Device deviceToUpdate = repo.findById(id).orElseThrow(() -> new DeviceNotFoundException(id));
        if (deviceToUpdate != null) {
            device.forEach((k,v) -> {
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
