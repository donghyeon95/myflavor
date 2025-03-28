package com.myflavor.myflavor.domain.restaurant.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantELKDTO;
import com.myflavor.myflavor.domain.restaurant.model.entity.RestaurantName;
import com.myflavor.myflavor.domain.restaurant.model.repository.RestaurantNameRepository;
import com.myflavor.myflavor.domain.restaurant.model.repository.RestaurantRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.DistanceUnit;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class SearchService {
	@Autowired
	ElasticsearchClient esClient;

	@Autowired
	RestaurantRepository restaurantRepository;

	@Autowired
	RestaurantNameRepository restaurantNameRepository;

	private final String restaurantIndexName = "restaurantCurrent";
	private final String fieldName = "location";

	public List<RestaurantELKDTO> boundarySearch(double boundaryKMeter, double latitude, double longitude) throws
		IOException {
		SearchResponse<RestaurantELKDTO> searchResponse = esClient.search(s -> s
			.index(restaurantIndexName)
			.size(1000)
			.scroll(Time.of(t -> t.time("2m")))
			.sort(so -> so
				.geoDistance(g -> g
					.field(fieldName)
					.order(SortOrder.Asc)
					.unit(DistanceUnit.Kilometers)
					.location(l -> l.latlon(v -> v
						.lat(latitude)
						.lon(longitude)
					))
				)
			)
			.query(q -> q
				.geoDistance(d -> d
					.field(fieldName)
					.distance(boundaryKMeter + "km")
					.location(l -> l
						.latlon(v -> v
							.lat(latitude)
							.lon(longitude)
						)
					)
				)), RestaurantELKDTO.class);

		System.out.println("total: " + searchResponse.hits().total());

		List<RestaurantELKDTO> result = new ArrayList<>();
		result.addAll(searchResponse.hits().hits().stream().map(Hit::source).toList());

		String scrollId = searchResponse.scrollId();

		// TODO 어떤 것을 내보낼 것인가?
		while (true) {
			String finalScrollId = scrollId;
			ScrollRequest scrollRequest = ScrollRequest.of(s -> s
				.scrollId(finalScrollId)
				.scroll(Time.of(t -> t.time("2m")))
			);
			ScrollResponse<RestaurantELKDTO> scrollResponse = esClient.scroll(scrollRequest, RestaurantELKDTO.class);

			if (scrollResponse.hits().hits().isEmpty())
				break;

			result.addAll(scrollResponse.hits().hits().stream().map(Hit::source).toList());
			scrollId = scrollResponse.scrollId();
		}

		System.out.println(result.getFirst());
		return result;
	}

	public List<RestaurantName> nameSearch(String name) {
		// TODO 검색어 저장
		// Redis 최근 검색 => (자주 검색하는 카테고리, 자주 검색하는 위치 등을 기록)
		// 검색 기록 저장
		return restaurantNameRepository.findByNameNative(name);
	}
}
