package com.myflavor.myflavor.domain.restaurant.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantDTO;
import com.myflavor.myflavor.domain.restaurant.model.Restaurant;
import com.myflavor.myflavor.domain.restaurant.model.RestaurantCategory;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantCategoryRepository;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantRepository;

@Service
public class InsertRDB {

	@Autowired
	ConvertDataService convertDataService;
	@Autowired
	RestaurantCategoryRepository restaurantCategoryRepository;
	@Autowired
	RestaurantRepository restaurantRepository;

	@Transactional
	public Restaurant insertRestaurantRDB(RestaurantDTO restaurantDTO) {
		Restaurant restaurant = convertDataService.convertRestaurant(restaurantDTO);

		// 카테고리에 해당하는 값이 있을 경우,
		if (restaurant.getRestaurantCategoryId() != null) {
			RestaurantCategory category = restaurant.getRestaurantCategoryId();
			if (category.getPk() == null) {
				// 카테고리가 이미 저장이 안되어 있을 경우.
				Optional<RestaurantCategory> existingCategory = restaurantCategoryRepository.findByCategoryName(
						category.getCategoryName());
				if (existingCategory.isPresent()) {
					// 카테고리 값이
					restaurant.setRestaurantCategoryId(existingCategory.get());
				} else {
					restaurantCategoryRepository.save(category);
				}
			} else {
				// 카테고리에 해당하는 값이 있을 경우.
				category = restaurantCategoryRepository.findById(category.getPk())
						.orElseThrow(() -> new RuntimeException("Category not found"));
				restaurant.setRestaurantCategoryId(category);
			}

		}
		return restaurantRepository.save(restaurant);
	}
}
