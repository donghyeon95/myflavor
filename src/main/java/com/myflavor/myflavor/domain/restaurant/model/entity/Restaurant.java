package com.myflavor.myflavor.domain.restaurant.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Restaurant {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long pk;
	private String managementNumber;
	private double latitude;
	private double longitude;
	private String restaurantPhoto;
	private String menuPhoto;
	private String streetAddress;
	private String streetNumber;
	private String restaurantName;
	private String tel;
	private String phone;
	private String status;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "restaurant_category_id", unique = false)
	private RestaurantCategory restaurantCategory;

	// @Version
	// private Long version;
}



