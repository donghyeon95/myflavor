package com.myflavor.myflavor.domain.restaurant.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = LocationDeserializer.class)
public class RestaurantDTO {
	private String name;

	private Location location;

	private String managementNumber;
	private String restaurant_photo;
	private String restaurant_name;
	private String menu_photo;
	private String street_address;
	private String street_number;
	private String tel;
	private String phone;
	private String status;

	private RestaurantCategoryId restaurant_category_id;
}

