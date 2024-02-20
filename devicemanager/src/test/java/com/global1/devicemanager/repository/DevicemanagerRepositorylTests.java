package com.global1.devicemanager.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.global1.devicemanager.model.Device;

@DataJpaTest
public class DevicemanagerRepositorylTests {

	@Autowired
	DevicemanagerRepository repo;

	@Test
	void givenBrand_WhenDevicesIsNotEmpty_ThenReturnSuccess() throws Exception {
		// given
		String brand = "Samsung";
		List<Device> devices = new ArrayList<Device>() {
			{
				add(new Device("S8", "Samsung"));
				add(new Device("S7", "Samsung"));
			}
		};

		// when
		repo.saveAll(devices);

		// assert
		List<Device> devicesInDatabase = repo.findByBrand(brand);
		assertThat(devicesInDatabase).containsExactlyInAnyOrderElementsOf(devices);
	}

	@Test
	void givenBrand_WhenDevicesAreMoreThanJustOneBrand_ThenReturnSuccess() throws Exception {
		// given
		String brand = "Huawei";
		Device huaweiDevice = new Device("P10", "Huawei");
		List<Device> devices = new ArrayList<Device>() {
			{
				add(new Device("S8", "Samsung"));
				add(new Device("S7", "Samsung"));
				add(huaweiDevice);
			}
		};

		// when
		repo.saveAll(devices);

		// assert
		List<Device> devicesInDatabase = repo.findByBrand(brand);
		assertThat(devicesInDatabase).hasSize(1).containsExactly(huaweiDevice);
	}

	@Test
	void givenBrand_WhenDevicesIsEmpty_ThenReturnSuccess() throws Exception {
		// given
		String brand = "Huawei";
		List<Device> devices = new ArrayList<Device>() {
			{
				add(new Device("S8", "Samsung"));
				add(new Device("S7", "Samsung"));
			}
		};

		// when
		repo.saveAll(devices);

		// assert
		List<Device> devicesInDatabase = repo.findByBrand(brand);
		assertThat(devicesInDatabase).isEmpty();
	}
}
