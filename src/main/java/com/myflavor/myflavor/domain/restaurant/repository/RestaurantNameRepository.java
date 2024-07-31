package com.myflavor.myflavor.domain.restaurant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.myflavor.myflavor.domain.restaurant.model.RestaurantName;

public interface RestaurantNameRepository extends JpaRepository<RestaurantName, Long> {
	@Query(value = "SELECT r.pk, r.restaurant_name, r.street_address, r.management_number FROM restaurant r join restaurant_category rc on r.restaurant_category_id = rc.pk WHERE MATCH(r.restaurant_name) AGAINST(+:restaurant_name in boolean mode)", nativeQuery = true)
	List<RestaurantName> findByNameNative(@Param("restaurant_name") String restaurantName);
}
