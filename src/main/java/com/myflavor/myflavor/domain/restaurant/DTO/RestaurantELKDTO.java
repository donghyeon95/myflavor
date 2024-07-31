package com.myflavor.myflavor.domain.restaurant.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantELKDTO {
	private String name;

	private String managementNumber;
	private Location location;
	private String street_address;
	private String street_number;
	private String status;

	private RestaurantCategoryId restaurant_category_id;
}
