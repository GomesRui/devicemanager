package com.global1.devicemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global1.devicemanager.DevicemanagerApplication;
import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.service.DevicemanagerService;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;

@SpringBootTest(classes = DevicemanagerApplication.class)
@AutoConfigureMockMvc 
class DevicemanagerControllerTests {
 
    private static final String BASE_URL = "/api/v1/devicemanager";

    @Autowired
    MockMvc mocker;

    @MockBean
    DevicemanagerService service;

    @Autowired
    DevicemanagerController controller;
    
    @Autowired
    ObjectMapper mapper;

    @Test
	void givenNoDeviceId_whenGetDevice_thenReturnOK() throws Exception {
	
		//when
		when(service.getDevices()).thenReturn(new ArrayList<Device>());
		
		//assert
		mocker.perform(get(BASE_URL + "/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));

        //when
		when(service.getDevices()).thenReturn(new ArrayList<Device>() {
            {
                add(new Device());
            }
        });
		
		//assert
		mocker.perform(get(BASE_URL + "/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
	}

    @Test
	void givenDeviceIdExists_whenGetDevice_thenReturnOK() throws Exception {

		//given
        Long id = 1L;
		Device device = new Device("S8", "Samsung");
		
		//when
		when(service.getDevice(id)).thenReturn(device);
		
		//assert
		String response = mocker.perform(get(BASE_URL + "/{id}", id)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

        assertEquals(device, mapper.readValue(response, Device.class));
	}

    @Test
	void givenDeviceIdNotExists_whenGetDevice_thenReturnNOT_FOUND() throws Exception {

		//given
        Long id = 1L;
		
		//when
		when(service.getDevice(id)).thenThrow(new DeviceNotFoundException(id));
		
		//assert
		mocker.perform(get(BASE_URL + "/{id}", id)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
	}

    @Test
	void givenDeviceBrand_whenGetDevice_thenReturnOK() throws Exception {

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
        List<Device> devicesInDatabase = devices.stream().filter(x -> x.getBrand().equals(brand)).toList();
		when(service.getDevice(brand)).thenReturn(devicesInDatabase);
		
		//assert
		String response = mocker.perform(get(BASE_URL + "/search")
        .queryParam("brand", brand)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andReturn().getResponse().getContentAsString();

        assertEquals(devicesInDatabase, mapper.readValue(response, new TypeReference<List<Device>>(){}));
	}

    @Test
	void givenDeviceBrandAndName_whenPostDevice_thenReturnOK() throws Exception {

		//given
        String brand = "Samsung";
        String name = "S8";
        Device device = new Device(1L, name, brand, Instant.now());
		
		//when
        String jsonContent = mapper.writeValueAsString(device);
		when(service.addDevice(device)).thenReturn(device);

		//assert
		mocker.perform(post(BASE_URL + "/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonContent)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.brand").value(brand))
        .andExpect(jsonPath("$.name").value(name));
	}
}
