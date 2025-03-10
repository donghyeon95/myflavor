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

