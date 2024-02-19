package com.global1.devicemanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.global1.devicemanager.DevicemanagerApplication;
import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.repository.DevicemanagerRepository;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;

@SpringBootTest(classes = DevicemanagerApplication.class)
class DevicemanagerServiceTests {

	@Mock
	DevicemanagerRepository repo;

	@InjectMocks
	DevicemanagerService service;

	@Test
	void givenDeviceID_WhenDeviceIsFound_ThenReturnSuccess() throws Exception {

		//given
		Optional<Device> device = Optional.of(new Device("S8", "Samsung"));

		//when
		when(repo.findById(device.get().getId())).thenReturn(device);

		//assert
		Device deviceInDatabase = service.getDevice(device.get().getId());
		assertEquals(device.get(), deviceInDatabase);

	}

	@Test
	void givenDeviceID_WhenDeviceIsNotFound_ThenReturnException() throws Exception {

		//given
		Long deviceId = 0L;

		//when
		when(repo.findById(deviceId)).thenThrow(DeviceNotFoundException.class);

		//assert
		assertThrows(DeviceNotFoundException.class, () -> service.getDevice(deviceId));

	}

	@Test
	void givenDevices_WhenDevicesIsNotEmpty_ThenReturnSuccess() throws Exception {
		//given
		Device device = new Device("S8", "Samsung");
		List<Device> devices = new ArrayList<Device>()
		{
			{
				add(device);
			}
		};

		//when
		when(repo.findAll()).thenReturn(devices);

		//assert
		List<Device> devicesInDatabase = service.getDevices();
		assertEquals(devices.size(), devicesInDatabase.size());
		assertEquals(devices.get(0), devicesInDatabase.get(0));
	}

	@Test
	void givenDevices_WhenDevicesIsEmpty_ThenReturnSuccess() throws Exception {
		//given
		List<Device> devices = new ArrayList<Device>();

		//when
		when(repo.findAll()).thenReturn(devices);

		//assert
		List<Device> devicesInDatabase = service.getDevices();
		assertEquals(devices.size(), devicesInDatabase.size());
	}

	@Test
	void givenBrand_WhenDevicesIsNotEmpty_ThenReturnSuccess() throws Exception {
		//given
		String brand = "Samsung";
		List<Device> devices = new ArrayList<Device>()
		{
			{
				add(new Device("S8", "Samsung"));
				add(new Device("S7", "Samsung"));
				add(new Device("P10", "Huawei"));
			}
		};

		//when
		when(repo.findByBrand(brand)).thenReturn(devices.stream().filter(x -> x.getBrand().equals(brand)).toList());

		//assert
		List<Device> devicesInDatabase = service.getDevice(brand);
		assertEquals(devices.size()-1, devicesInDatabase.size());
	}

	@Test
	void testAddDevice() throws Exception {

		//given
		Device device = new Device("S8", "Samsung");
		
		//when
		when(repo.save(device)).thenReturn(device);
		
		//assert
		Device deviceInDatabase = service.addDevice(device);
		assertEquals(device, deviceInDatabase);
	}

	@Test
	void testUpdateDeviceFull() throws Exception {

		//given
		Device device = new Device(1L, "S8", "Samsung", Instant.now());
		Device updatedDevice = new Device(1L, "P10", "Huawei", Instant.now());
		
		//when
		when(repo.findById(anyLong())).thenReturn(Optional.of(device));
		when(repo.save(any())).thenReturn(updatedDevice);

		//assert
		Device deviceInDatabase = service.updateDevice(updatedDevice, device.getId());
		assertEquals(device.getId(), deviceInDatabase.getId());
		assertEquals(device.getBrand(), deviceInDatabase.getBrand());
		assertEquals(device.getName(), deviceInDatabase.getName());
	}

	@Test
	void testUpdateDevicePartial() throws Exception {

		//given
		Device device = new Device(1L, "S8", "Samsung", Instant.now());
		Device updatedDevice = new Device(1L, "S7", "Samsung", Instant.now());
		String json_device = "{" + "\"name\":" + "\"S7\"" + "}";
		Map<String,Object> updatedParamDevice = new ObjectMapper().readValue(json_device, HashMap.class);
		
		//when
		when(repo.findById(anyLong())).thenReturn(Optional.of(device));
		when(repo.save(any())).thenReturn(updatedDevice);

		//assert
		Device deviceInDatabase = service.updateDevice(updatedParamDevice, device.getId());
		assertEquals(device.getId(), deviceInDatabase.getId());
		assertEquals(device.getBrand(), deviceInDatabase.getBrand());
		assertEquals(device.getName(), deviceInDatabase.getName());
	}

	@Test
	void testDeleteDevice() throws Exception {

		//given
		Long id = 1L;
		
		//when
		when(repo.findById(anyLong())).thenThrow(DeviceNotFoundException.class);

		//assert
		assertThrows(DeviceNotFoundException.class, () -> service.deleteDevice(id));

	}


}
