package com.myflavor.myflavor.domain.restaurant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantDTO;
import com.myflavor.myflavor.domain.restaurant.model.entity.Restaurant;
import com.myflavor.myflavor.domain.restaurant.model.entity.RestaurantCategory;
import com.myflavor.myflavor.domain.restaurant.model.repository.RestaurantRepository;

@Service
public class InsertRDB {

	@Autowired
	ConvertDataService convertDataService;
	@Autowired
	RestaurantRepository restaurantRepository;
	@Autowired
	CategoryService categoryService;

	// 	@Transactional
	// 	public Restaurant insertRestaurantRDB(RestaurantDTO restaurantDTO) {
	// 		Restaurant restaurant = convertDataService.convertRestaurant(restaurantDTO);
	//
	// 		// 카테고리에 해당하는 값이 있을 경우,
	// 		if (restaurant.getRestaurantCategoryId() != null) {
	// 			System.out.println("a");
	// 			RestaurantCategory category = restaurant.getRestaurantCategoryId();
	// 			if (category.getPk() == null) {
	// 				// 카테고리가 이미 저장이 안되어 있을 경우.
	// 				System.out.println("b");
	// 				// Optional<RestaurantCategory> existingCategory = restaurantCategoryRepository.findByCategoryName(
	// 				// 	category.getCategoryName());
	// 				// if (existingCategory.isPresent()) {
	// 				// 	// 카테고리 값이
	// 				// 	System.out.println("c");
	// 				// 	restaurant.setRestaurantCategoryId(existingCategory.get());
	// 				// } else {
	// 				// 	System.out.println("d");
	// 				restaurantCategoryRepository.saveAndFlush(category);
	// 				// }
	// 			} else {
	// 				// 카테고리에 해당하는 값이 없을 경우.
	// 				// System.out.println("e");
	// 				// category = restaurantCategoryRepository.findById(category.getPk())
	// 				// 	.orElseThrow(() -> new RuntimeException("Category not found"));
	// 				// restaurant.setRestaurantCategoryId(category);
	// 			}
	// 		}
	//
	// 		return restaurantRepository.save(restaurant);
	// 	}
	// }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Restaurant insertRestaurantRDB(RestaurantDTO restaurantDTO) {
		RestaurantCategory category;
		try {
			category = categoryService.findCategory(restaurantDTO.getRestaurant_category_id().getCategory_name());
			// categoryService.saveCategory(category);

		} catch (DataIntegrityViolationException e) {
			category = categoryService.findCategory(restaurantDTO.getRestaurant_category_id().getCategory_name());
		}

		Restaurant restaurant = convertDataService.convertRestaurant(restaurantDTO);
		restaurant.setRestaurantCategoryId(category);

		return restaurantRepository.saveAndFlush(restaurant);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void insertAllRestaurantRDB(List<RestaurantDTO> restaurantDTOS) {

	}

}

