package com.myflavor.myflavor.domain.restaurant.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.myflavor.myflavor.domain.restaurant.DTO.RestaurantDTO;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantCategoryRepository;
import com.myflavor.myflavor.domain.restaurant.repository.RestaurantRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.indices.Alias;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.GetAliasRequest;
import co.elastic.clients.elasticsearch.indices.GetAliasResponse;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesRequest;
import co.elastic.clients.elasticsearch.indices.UpdateAliasesResponse;
import jakarta.persistence.OptimisticLockException;

@Service
@EnableAsync
public class RestautantGetter {
	@Autowired
	private Environment env;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ElasticsearchClient esClient;
	@Autowired
	private RestaurantRepository restaurantRepository;
	@Autowired
	private RestaurantCategoryRepository restaurantCategoryRepository;
	@Autowired
	ConvertDataService convertDataService;
	@Autowired
	InsertRDB insertRDB;

	// 비동기 쓰레드 풀 생성
	// 이것이 없다면 기본 thread 풀 사용 => 10개
	private final ExecutorService executor = Executors.newFixedThreadPool(5);
	private String restaurantIndexName = "restaurant-";
	private String aliasIndex = "restaurantCurrent";
	private int startIndex = 0;
	private int totalCount = 0;

	//  @Scheduled(cron = "0 0 0 * * *")
	//  public void updateSeoulData() {
	//
	//  }

	public void insertSeoulData() throws IOException {
		String todayRestaurantIndexName = restaurantIndexName + LocalDate.now();
		System.out.println(todayRestaurantIndexName);

		// Index 확인 후 인덱스가 없다면 인덱스 생성
		// if (!hasIndex(todayRestaurantIndexName)) {
		// 	setElasticIndex(todayRestaurantIndexName);
		// }

		// 맨처음 1개의 데이터에서 전체 데이터 길이를 받아온다.
		int restaurantLength = getLength();
		List<CompletableFuture<List<RestaurantDTO>>> asyncList = new ArrayList<>();

		System.out.println("restaurantLength: " + restaurantLength);

		long beforeTime = System.currentTimeMillis();
		List<RestaurantDTO> restaurantDTOList = new ArrayList<>();
		for (int i = 1; i < restaurantLength; i += 1000) {
			// 비동기 함수 생성
			int finalI = i;
			int finalI1 = i;

			final String threadNumber = Integer.toString(i);
			CompletableFuture<List<RestaurantDTO>> future = CompletableFuture.supplyAsync(() -> {
				final String threadName = "thread " + threadNumber;
				// 비동기 작업
				try {
					List<RestaurantDTO> restaurantList = getSeoulData(finalI, finalI1 + 999);
					restaurantDTOList.addAll(restaurantList);
					for (RestaurantDTO restaurant : restaurantList) {
						// UpdateResponse<RestaurantDTO> response = insertRestaurantELK(restaurant,
						// 	todayRestaurantIndexName);
						// System.out.println(Thread.currentThread().getName() + " " + restaurant);
						int retryCnt = 0;

						while (retryCnt < 3) {
							// System.out.println("retryCnt: " + retryCnt);
							try {
								// System.out.println(Thread.currentThread().getName());
								insertRDB.insertRestaurantRDB(restaurant);
								break;
							} catch (OptimisticLockException e) {
								// retry 로직
								retryCnt++;
								System.out.println("낙관적 락 발생");
								System.out.println(e);
							} catch (DataIntegrityViolationException e) {
								System.out.println("Unique exception catch");
								retryCnt++;
							}
						}

						// TODO  response update 실패 시
					}

					return restaurantList;
				} catch (IOException e) {
					// async 작업 실패 시 =>
					throw new RuntimeException(e);
				} catch (Exception e) {
					System.out.printf("에러");
					System.out.println(e);
				}

				return null;
			}, executor);

			asyncList.add(future);
		}

		// TODO 위 작업이 모두 종료가 되었는 지 확인하는 방법
		CompletableFuture.allOf(asyncList.toArray(new CompletableFuture[0]))
			.thenRun(() -> {
				long afterTime = System.currentTimeMillis();
				long secDiffTime = (afterTime - beforeTime) / 1000;
				System.out.println("시간차이(s) : " + secDiffTime);

				// long b_time = System.currentTimeMillis();
				// for (RestaurantDTO restaurantDTO : restaurantDTOList) {
				// 	insertRDB.insertRestaurantRDB(restaurantDTO);
				// }
				// long a_time = System.currentTimeMillis();
				// System.out.println("rdb input에 걸린 시간: " + (a_time - b_time));
				System.out.println("RDB INSERT IS END. RestaurantCount: " + restaurantLength);
			});
	}

	public int getLength() {
		String rawUri = env.getProperty("openApi.seoul.jsonUrl");
		String serviceName = env.getProperty("openApi.seoul.serviceName");
		String openUri = rawUri + "/" + 1 + "/" + 1;

		ResponseEntity<JsonNode> responseNode = restTemplate.exchange(openUri, HttpMethod.GET, null, JsonNode.class);
		JsonNode body = responseNode.getBody();

		// FIXME if NULL이 나왔을 경우, 어떻게 처리를 해야 할까?
		return Objects.requireNonNull(body).get(serviceName).get("list_total_count").asInt();
	}

	public UpdateResponse<RestaurantDTO> insertRestaurantELK(RestaurantDTO restaurant, String indexname) throws
		IOException {
		UpdateRequest<RestaurantDTO, RestaurantDTO> updateRequest = UpdateRequest.of(u -> u
			.index(indexname)
			.id(restaurant.getManagementNumber())
			.doc(restaurant)
			.docAsUpsert(true)
		);

		return esClient.update(updateRequest, RestaurantDTO.class);
	}

	public void deleteELKIndex() throws IOException {
		Query query = Query.of(q -> q.matchAll(m -> m));

		DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(d -> d
			.index(restaurantIndexName)
			.query(query)
		);
		DeleteByQueryResponse deleteByQueryResponse = esClient.deleteByQuery(deleteByQueryRequest);
		// System.out.println("Document Deleted: " + deleteByQueryResponse.deleted());
	}

	public List<RestaurantDTO> getSeoulData(int startIndex, int endIndex) throws IOException {
		String rawUri = env.getProperty("openApi.seoul.jsonUrl");
		String openUri = rawUri + "/" + startIndex + "/" + endIndex;
		// System.out.println(openUri);

		// 이거 restTemplate 자체를 비동기 요청으로 변경.
		ResponseEntity<JsonNode> responseNode = restTemplate.exchange(openUri, HttpMethod.GET, null, JsonNode.class);
		return parsingJsonObject(responseNode.getBody());
	}

	public List<RestaurantDTO> parsingJsonObject(JsonNode jsonNode) throws IOException {
		//    System.out.println(jsonNode);
		String serviceName = env.getProperty("openApi.seoul.serviceName");

		ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

		ArrayNode arrayNode = (ArrayNode)jsonNode.get(serviceName).get("row");
		List<RestaurantDTO> result = new ArrayList<>();

		// Deserialize API JsonObject
		for (JsonNode node : arrayNode) {
			RestaurantDTO restaurantDTO =
				objectMapper.treeToValue(node, RestaurantDTO.class);
			result.add(restaurantDTO);
		}

		return result;
	}

	private boolean hasIndex(String indexName) throws IOException {
		return esClient.indices().exists(existsRequest -> existsRequest.index(indexName)).value();
	}

	private void setElasticIndex(String indexName) throws IOException {
		// 기존의 별칭을 사용하는 인덱스를 찾는다.
		GetAliasRequest getAliasRequest = GetAliasRequest.of(g -> g.name(aliasIndex));
		GetAliasResponse getAliasResponse = esClient.indices().getAlias(getAliasRequest);
		String oldIndexName = getAliasResponse.result()
			.keySet()
			.stream()
			.findFirst()
			.orElseThrow(() -> new IOException("No Index found with alias" + aliasIndex));

		// location 정보 매핑
		TypeMapping mapping = TypeMapping.of(builder -> builder
			.properties("location", p -> p.geoPoint(g -> g))
		);

		// index 세팅 설정
		IndexSettings settings = IndexSettings.of(setting -> setting
			.numberOfShards("1")
			.numberOfReplicas("1")
		);

		// index 생성 request
		CreateIndexRequest createIndexRequest = CreateIndexRequest.of(c -> c
			.index(indexName)
			.aliases(restaurantIndexName + "Current", Alias.of(a -> a))
			.settings(settings)
			.mappings(mapping)
		);

		// alias 삭제 requset
		UpdateAliasesRequest updateAliasesRequest = UpdateAliasesRequest.of(u -> u
			.actions(a -> a
				.remove(r -> r
					.index(oldIndexName)
					.alias(aliasIndex)
				)
			)
		);

		try {
			CreateIndexResponse createIndexResponse = esClient.indices().create(createIndexRequest);
			// System.out.println("Index Creation responses: " + createIndexResponse.acknowledged());

			// 만약 새로운 것이 생성이 되었다면 기존의 별칭 삭제
			UpdateAliasesResponse updateAliasesResponse = esClient.indices().updateAliases(updateAliasesRequest);

			if (!updateAliasesResponse.acknowledged()) {
				// TODO Retry 로직 필요.
				throw new IOException("Failed to update alias: " + aliasIndex);
			}
		} catch (IOException error) {
			error.printStackTrace();
		}

	}
}
