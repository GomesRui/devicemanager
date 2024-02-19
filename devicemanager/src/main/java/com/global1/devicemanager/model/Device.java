package com.global1.devicemanager.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_DEVICES")
@Getter @NoArgsConstructor
public class Device {  
    @Id 
	@GeneratedValue(strategy = GenerationType.AUTO) 
	private Long id;

    @Setter 
    @NotNull
    @NotBlank(message = "The device needs to have a name!")
    @Column(updatable=true, unique=true)
    private String name;

    @Setter 
    @NotNull 
    @NotBlank(message = "The device needs to have a brand!")
    @Column(updatable=true)
    private String brand;

    @CreationTimestamp
    private Instant created;

    public Device(String name, String brand) {
        this.name = name;
        this.brand = brand;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((brand == null) ? 0 : brand.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (brand == null) {
			if (other.brand != null)
				return false;
		} else if (!brand.equals(other.brand))
			return false;
		return true;
	}

    
}
