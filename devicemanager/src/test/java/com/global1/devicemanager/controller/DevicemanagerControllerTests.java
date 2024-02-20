package com.global1.devicemanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global1.devicemanager.DevicemanagerApplication;
import com.global1.devicemanager.model.Device;
import com.global1.devicemanager.service.DevicemanagerService;
import com.global1.devicemanager.util.exception.DeviceNotFoundException;
import com.global1.devicemanager.util.exception.FieldNotValidatedException;

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

        // when
        when(service.getDevices()).thenReturn(new ArrayList<Device>());

        // assert
        mocker.perform(get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // when
        when(service.getDevices()).thenReturn(new ArrayList<Device>() {
            {
                add(new Device());
            }
        });

        // assert
        mocker.perform(get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void givenDeviceIdExists_whenGetDevice_thenReturnOK() throws Exception {

        // given
        Long id = 1L;
        Device device = new Device("S8", "Samsung");

        // when
        when(service.getDevice(id)).thenReturn(device);

        // assert
        String response = mocker.perform(get(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(device, mapper.readValue(response, Device.class));
    }

    @Test
    void givenDeviceIdNotExists_whenGetDevice_thenReturnNOT_FOUND() throws Exception {

        // given
        Long id = 1L;

        // when
        when(service.getDevice(id)).thenThrow(new DeviceNotFoundException(id));

        // assert
        mocker.perform(get(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDeviceBrand_whenGetDevice_thenReturnOK() throws Exception {

        // given
        String brand = "Samsung";
        List<Device> devices = new ArrayList<Device>() {
            {
                add(new Device("S8", "Samsung"));
                add(new Device("S7", "Samsung"));
                add(new Device("P10", "Huawei"));
            }
        };

        // when
        List<Device> devicesInDatabase = devices.stream().filter(x -> x.getBrand().equals(brand)).toList();
        when(service.getDevice(eq(brand), any(Errors.class))).thenReturn(devicesInDatabase);

        // assert
        String response = mocker.perform(get(BASE_URL + "/search")
                .queryParam("brand", brand)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn().getResponse().getContentAsString();

        assertEquals(devicesInDatabase, mapper.readValue(response, new TypeReference<List<Device>>() {
        }));
    }

    @Test
    void givenDeviceBrandAndName_whenPostDevice_thenReturnOK() throws Exception {

        // given
        String brand = "Samsung";
        String name = "S8";
        Device device = new Device(name, brand);

        Field idField = device.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(device, 1L);

        // when
        String jsonContent = mapper.writeValueAsString(device);
        when(service.addDevice(eq(device), any(Errors.class))).thenReturn(device);

        // assert
        mocker.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value(brand))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void givenDeviceWithBrand_whenPostDevice_thenReturnBAD_REQUEST() throws Exception {

        // given
        String brand = "Samsung";

        // when
        String jsonContent = "{\"brand\":\"" + brand + "\"}";
        when(service.addDevice(any(Device.class), any(Errors.class)))
                .thenThrow(new FieldNotValidatedException("name", "must not be null"));

        // assert
        mocker.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Field \"name\" failed validation: must not be null"));
    }

    @Test
    void givenDevice_whenPutDevice_thenReturnOK() throws Exception {

        // given
        Long id = 1L;
        String name = "P10";
        String brand = "Huawei";
        Device device = new Device("S8", "Samsung");
        Device updatedDevice = new Device(name, brand);

        Field idField = device.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(device, id);
        idField.set(updatedDevice, id);

        // when
        String jsonContent = mapper.writeValueAsString(device);
        when(service.updateDevice(eq(updatedDevice), eq(device.getId()), any(Errors.class))).thenReturn(updatedDevice);

        // assert
        mocker.perform(put(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value(brand))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void givenDevice_whenPatchDevice_thenReturnOK() throws Exception {

        // given
        Long id = 1L;
        String name = "S7";
        Device device = new Device("S8", "Samsung");
        Device updatedDevice = new Device(name, "Samsung");

        Field idField = device.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(device, id);
        idField.set(updatedDevice, id);

        // when
        String json_device = "{" + "\"name\":" + "\"" + name + "\"" + "}";
        Map<String, Object> updatedParamDevice = new ObjectMapper().readValue(json_device, HashMap.class);
        when(service.updateDevice(eq(updatedParamDevice), eq(device.getId()), any(Errors.class)))
                .thenReturn(updatedDevice);

        // assert
        mocker.perform(patch(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json_device)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Samsung"))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void givenDeviceID_whenDeleteDevice_thenReturnOK() throws Exception {

        // given
        Long id = 1L;

        // when
        doThrow(new DeviceNotFoundException(id)).when(service).deleteDevice(id);

        // assert
        mocker.perform(delete(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Device was deleted for id " + id));
    }

}
