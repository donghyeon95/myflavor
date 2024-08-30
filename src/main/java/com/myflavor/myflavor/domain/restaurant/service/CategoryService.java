package com.myflavor.myflavor.domain.restaurant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.restaurant.model.RestaurantCategory;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantCategoryRepository;

@Service
public class CategoryService {

	@Autowired
	RestaurantCategoryRepository restaurantCategoryRepository;

	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public RestaurantCategory findCategory(String categoryName) {
		return restaurantCategoryRepository.findByCategoryName(categoryName)
			.orElseGet(() -> {
				RestaurantCategory newCategory = RestaurantCategory.builder()
					.categoryName(categoryName)
					.build();
				// return restaurantCategoryRepository.saveAndFlush(newCategory);
				return newCategory;
			});
	}

	// @Transactional(propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public RestaurantCategory saveCategory(RestaurantCategory category) {
		return restaurantCategoryRepository.saveAndFlush(category);
	}
}
