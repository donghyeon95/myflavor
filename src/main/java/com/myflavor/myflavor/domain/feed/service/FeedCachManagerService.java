package com.myflavor.myflavor.domain.feed.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;

/**
 * FEED 조회 Cache을 관리하는 서비스
 */

@Service
public class FeedCachManagerService {
	private final String RECOMMEND_FEED_CACHE_SCORE = "user:feed_cache_score:";
	private final String RECOMMEND_FEED_CACHE = "user:feed_cache:";

	private final RedisTemplate<String, Object> redisTemplate;

	public FeedCachManagerService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * FEED CACHE 저장
	 * FEED CACHE 저장한다.
	 */
	// FIXME => candiate에 있는 데이\터의 경우 어떻게 처리할 것인가?
	// FIXME => 이 방식이면 중복된 데이터가 있을 수도 있는 데 어떻게 처리할 것인가?
	// FIXME => 두개 트랜젝션 처리 (WATCH랑 MULTI를 사용한다고 가정)
	// FIXME => bulk로 업데이트가 되어야 되는데, 두쌍에 대한 transaction만 유지하면됨. -> 한쌍이 문제가 있다고 해서 다른쌍이 저장이 안되면 안됨.
	// 여기서 시간이 많이 소모되게 되면 성능 이슈가 날 거 같은데... 이것을 어떻게 해결을 해야 하나? 비동기????? 벌크???
	public void saveBulkFeedCache(String userName, List<MainFeedDTO> candidates, List<Long> scores) {
		if (candidates.size() != scores.size()) {
			throw new IllegalArgumentException("Candidates and scores list must have the same size");
		}

		Set<Integer> successIndice = new HashSet<>();
		int retryCnt = 3; // 최대 재시도 횟수

		// 이거도 bulk로 할 수 있는 방법이 없을까? => 저장이 너무 자주 일어나는데...
		for (int attemp = 0; attemp < retryCnt; attemp++) {
			List<Integer> retryIndices = getRetryIndices(candidates.size(), successIndice);
			if (retryIndices.isEmpty())
				break;

			for (int i : retryIndices) {
				boolean success = saveSingleFeedTransaction(userName, candidates.get(i), scores.get(i));
				if (success)
					successIndice.add(i);
			}
		}
		//FIXME 세번이 지나고도 실패한 로직은 어떻게 할 것인가?
		// 지금은 무시하는 것으로.
	}

	/**
	 * 📌 실패한 데이터만 재시도하기 위해 처리할 인덱스 가져오기
	 */
	private List<Integer> getRetryIndices(int size, Set<Integer> successIndice) {
		List<Integer> retryIndices = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			if (!successIndice.contains(i)) {
				retryIndices.add(i);
			}
		}
		return retryIndices;
	}

	/**
	 * Feed Cache 저장 Transaction
	 */
	private boolean saveSingleFeedTransaction(String userName, MainFeedDTO candidate, long score) {
		String feedCacheKey = getFeedCacheKey(userName);
		String scoreCacheKey = getScoreCacheKey(userName);
		String id = candidate.getId() + "_" + candidate.getCreatedAt();

		List<Object> results = redisTemplate.execute((RedisCallback<List<Object>>)connection -> {
			connection.multi(); // 트랜젝션 시작

			saveFeedScoreToCache(scoreCacheKey, id, score, connection);
			saveFeedToCache(feedCacheKey, id, candidate, connection);

			return connection.exec();
		});

		return results != null && !results.isEmpty();
	}

	/**
	 * FEED SCORE CACHE 저장 (ZSET)
	 */
	private void saveFeedScoreToCache(String scoreCacheKey, String id, long score, RedisConnection connection) {
		connection.zAdd(scoreCacheKey.getBytes(), (double)score, id.getBytes());
	}

	/**
	 * FEED CACHE 저장 (HASH)
	 */
	private void saveFeedToCache(String feedCacheKey, String id, MainFeedDTO candidate, RedisConnection connection) {
		try {
			String candidateJson = new ObjectMapper().writeValueAsString(candidate);
			connection.hSet(feedCacheKey.getBytes(), id.getBytes(), candidateJson.getBytes());
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize MainFeedDTO", e);
		}
	}

	/**
	 * FEED 캐시 히트 여부 확인
	 *
	 */

	/**
	 * FEED 캐시 최신 시간 확인 (hot / 쿨)
	 * scan을 활용하여 => 최근 시간 확인
	 */
	public LocalDateTime getLatestCacheTime(String userName) {
		// cache 키
		String offsetKey = RECOMMEND_FEED_CACHE_SCORE + userName;

		Cursor<ZSetOperations.TypedTuple<Object>> cursor = redisTemplate.opsForZSet()
			.scan(offsetKey, ScanOptions.scanOptions().match("*").count(1000).build());

		return StreamSupport.stream(cursor.spliterator(), false)
			.map(ZSetOperations.TypedTuple::getValue)
			.map(key -> {
				String dateTimeStr = String.valueOf(key).split("_")[1]; // "2025-03-14T22:39:58.671588"
				return LocalDateTime.parse(dateTimeStr); // String -> LocalDateTime 변환
			})
			.map(time -> time.toEpochSecond(ZoneOffset.UTC))
			.max(Long::compareTo)
			.map(epoch -> LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC))
			.orElse(LocalDateTime.now().minusDays(7));
	}

	/**
	 * FEED 핫 캐시 > 쿨캐시 전환 Handler
	 */

	/**
	 * hot 캐시 업데이트
	 */

	/**
	 * 캐시에 저장된 feedId offset 조회
	 */
	public List<String> getFeedIds(String userName, long pageOffset, long pageSize) {
		// cache 키
		String offsetKey = getScoreCacheKey(userName);
		return redisTemplate.opsForZSet()
			.reverseRange(offsetKey, pageOffset, pageOffset + pageSize - 1)
			.stream()
			.filter(Objects::nonNull)
			.map(String::valueOf)
			.toList();
	}

	/**
	 * 캐시에서 Id 기준으로 큭정 데이터 가져오기
	 * hashkey -> feedId + " : " + 시간
	 */
	public <T> List<T> getMainFeedsFromIds(String userName, List<String> feedIds, Class<T> clazz) {
		String feedCacheKey = getFeedCacheKey(userName);

		return redisTemplate.opsForHash().multiGet(feedCacheKey, new ArrayList<>(feedIds))
			.stream().map(obj -> new ObjectMapper().convertValue(obj, clazz)).toList();
	}

	/**
	 * 캐시 키 관리 메서드
	 */
	private String getFeedCacheKey(String userName) {
		return RECOMMEND_FEED_CACHE + userName;
	}

	private String getScoreCacheKey(String userName) {
		return RECOMMEND_FEED_CACHE_SCORE + userName;
	}

}

/**
 * 📌 실패 데이터를 저장하기 위한 DTO 클래스
 */
class FailedFeedDTO {
	private String userName;
	private MainFeedDTO candidate;
	private long score;

	public FailedFeedDTO(String userName, MainFeedDTO candidate, long score) {
		this.userName = userName;
		this.candidate = candidate;
		this.score = score;
	}

	public String getUserName() {
		return userName;
	}

	public MainFeedDTO getCandidate() {
		return candidate;
	}

	public long getScore() {
		return score;
	}
}