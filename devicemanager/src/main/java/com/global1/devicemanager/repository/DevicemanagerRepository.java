package com.global1.devicemanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;

import java.util.List;


public interface DevicemanagerRepository extends JpaRepository<Device, Long>{
    List<Device> findByBrand(String brand);
}
