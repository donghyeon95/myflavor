package com.myflavor.myflavor.domain.restaurant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.restaurant.model.RestaurantCategory;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, Long> {
	// @Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<RestaurantCategory> findByCategoryName(String categoryName);
}
