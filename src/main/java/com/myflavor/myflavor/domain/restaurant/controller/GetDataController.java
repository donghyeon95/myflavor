package com.myflavor.myflavor.domain.restaurant.controller;

import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantELKDTO;
import com.myflavor.myflavor.domain.restaurant.model.RestaurantName;
import com.myflavor.myflavor.domain.restaurant.service.RestautantGetter;
import com.myflavor.myflavor.domain.restaurant.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping(value = "/openapi", produces = "application/json")
public class GetDataController {

	@Autowired
	private RestautantGetter restautantGetter;
	@Autowired
	private SearchService searchService;

	@GetMapping("/search/map")
	public List<RestaurantELKDTO> getMapData(@RequestParam("lo") Double longitude, @RequestParam("la") Double latitude,
			@RequestParam("b") Double boundarySize) throws IOException {
		return searchService.boundarySearch(boundarySize, latitude, longitude);
	}

	@GetMapping("/search/restaurant")
	public List<RestaurantName> getRestaurant(@RequestParam("n") String name) {
		// TODO name에 대한 검증
		return searchService.nameSearch(name);
	}

	@GetMapping("/insert")
	public void insertSeoulData() throws IOException {
		restautantGetter.insertSeoulData();
	}

	@DeleteMapping()
	public void deleteData() throws IOException {
		restautantGetter.deleteELKIndex();
	}

}
