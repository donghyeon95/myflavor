package com.myflavor.myflavor.domain.restaurant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantName {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long pk;
	private String managementNumber;
	private String streetAddress;
	private String restaurantName;

}
