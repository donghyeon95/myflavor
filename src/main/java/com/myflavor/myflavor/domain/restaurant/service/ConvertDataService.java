package com.myflavor.myflavor.domain.restaurant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantDTO;
import com.myflavor.myflavor.domain.restaurant.model.Restaurant;
import com.myflavor.myflavor.domain.restaurant.model.RestaurantCategory;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantCategoryRepository;

@Service
public class ConvertDataService {

	@Autowired
	RestaurantCategoryRepository restaurantCategoryRepository;
	@Autowired
	CategoryService categoryService;

	// @Transactional
	@Transactional
	public Restaurant convertRestaurant(RestaurantDTO restaurantDTO) {
		// restaurant 카테고리 데이터 획득
		// RestaurantCategory category = restaurantCategoryRepository.findByCategoryName(
		// 		restaurantDTO.getRestaurant_category_id().getCategory_name())
		// 	// 데이터가 없을 경우 새로운 객체를 생성해서 데이터 반영
		// 	.orElse(RestaurantCategory.builder()
		// 		.categoryName(restaurantDTO.getRestaurant_category_id().getCategory_name())
		// 		.build());
		RestaurantCategory category = categoryService.findCategory(
			restaurantDTO.getRestaurant_category_id().getCategory_name());

		return Restaurant.builder()
			.latitude(restaurantDTO.getLocation().getLat())
			.longitude(restaurantDTO.getLocation().getLon())
			.streetAddress(restaurantDTO.getStreet_address())
			.streetNumber(restaurantDTO.getStreet_number())
			.phone(restaurantDTO.getPhone())
			.restaurantPhoto(restaurantDTO.getRestaurant_photo())
			.menuPhoto(restaurantDTO.getMenu_photo())
			.tel(restaurantDTO.getTel())
			.restaurantName(restaurantDTO.getName())
			.status(restaurantDTO.getStatus())
			.restaurantCategoryId(category)
			.managementNumber(restaurantDTO.getManagementNumber())
			.build();
	}
}
