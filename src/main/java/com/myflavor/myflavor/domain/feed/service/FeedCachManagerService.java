package com.myflavor.myflavor.domain.feed.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;

/**
 * FEED 조회 Cache을 관리하는 서비스
 */

@Service
public class FeedCachManagerService implements MessageListener {
	private final String RECOMMEND_FEED_CACHE_SCORE = "user:feed_cache_score:";
	private final String RECOMMEND_FEED_CACHE = "user:feed_cache:";
	private final String RECOMMEND_FEED_TTL_CACHE = "user:feed_cache_ttl:";

	private final String RECOMMEND_FEED_COOL_CACHE_SCORE = "user:feed_cool_cache_score:";

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

		// hot Cache에 대한 10분 TTL
		// 이렇게 일괄적으로 적용하게 되면 => 계속 요청이 오는 경우에는 hot 캐시가 삭제가 안되는 경우가 생김
		// 개별 적용을 해줘야 할 수도 있다.
		redisTemplate.opsForValue()
			.set(this.RECOMMEND_FEED_TTL_CACHE + userName, "temp", Duration.ofMinutes(10));

		//FIXME 세번이 지나고도 실패한 로직은 어떻게 할 것인가?
		// 지금은 무시하는 것으로.
	}

	/**
	 * 실패한 데이터만 재시도하기 위해 처리할 인덱스 가져오기
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

			try {
				saveFeedScoreToCache(scoreCacheKey, id, score, connection);
				saveFeedToCache(feedCacheKey, id, candidate, connection);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

			return connection.exec();
		});

		return results != null && !results.isEmpty();
	}

	/**
	 * FEED SCORE CACHE 저장 (ZSET)
	 */
	private void saveFeedScoreToCache(String scoreCacheKey, String id, long score, RedisConnection connection) throws
		JsonProcessingException {
		try {
			// id는 "feedId_2025-04-08T15:20:00" 형식 같은 거
			String memberJson = new ObjectMapper().writeValueAsString(id);  // 예: "\"feedId_2025-04-08T15:20:00\""
			connection.zAdd(scoreCacheKey.getBytes(), (double)score, memberJson.getBytes());
		} catch (Exception e) {
			throw new RuntimeException("Failed to serialize ZSet member", e);
		}
	}

	/**
	 * FEED CACHE 저장 (HASH)
	 */
	private void saveFeedToCache(String feedCacheKey, String id, MainFeedDTO candidate, RedisConnection connection) {
		try {
			RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>)redisTemplate.getHashValueSerializer();

			byte[] valueBytes = valueSerializer.serialize(candidate); // 이제 경고 사라짐
			connection.hSet(feedCacheKey.getBytes(), id.getBytes(), valueBytes);
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
			.filter(Objects::nonNull)
			.map(Object::toString)
			.map(key -> {
				String dateTimeStr = String.valueOf(key).split("_")[1]; // "2025-03-14T22:39:58.671588"
				return LocalDateTime.parse(dateTimeStr); // String -> LocalDateTime 변환
			})
			.map(time -> time.toEpochSecond(ZoneOffset.UTC))
			.max(Long::compareTo)
			.map(epoch -> LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC))
			.orElse(LocalDateTime.now().minusDays(50)); //TODO 7일로 변경 필요
	}

	/**
	 * FEED 핫 캐시 > 쿨캐시 전환 Handler
	 */
	@Override
	public void onMessage(Message message, byte[] pattern) {
		// Redis HOT-CACEH에서 삭제 TTL이 오게 되면 이전에 Redis에 있던 데이터 삭제
		try {
			String expiredKey = message.toString();
			System.out.println("feedCache: " + expiredKey);

			if (expiredKey.startsWith(RECOMMEND_FEED_TTL_CACHE)) {
				String userName = expiredKey.substring(RECOMMEND_FEED_TTL_CACHE.length());

				try {
					redisTemplate.delete(RECOMMEND_FEED_CACHE + userName);
				} catch (Exception e) {
					System.err.println("⚠️ Hash delete error: " + e.getMessage());
				}

				try {
					Set<ZSetOperations.TypedTuple<Object>> scores = Optional.ofNullable(
						redisTemplate.opsForZSet()
							.rangeWithScores(RECOMMEND_FEED_CACHE_SCORE + userName, 0, -1)
					).orElse(Collections.emptySet());

					for (ZSetOperations.TypedTuple<Object> rank : scores) {
						if (rank != null && rank.getValue() != null && rank.getScore() != null) {
							try {
								redisTemplate.opsForZSet()
									.add(RECOMMEND_FEED_COOL_CACHE_SCORE + userName, rank.getValue(), rank.getScore());
							} catch (Exception e) {
								System.err.println("⚠️ Cool cache insert fail: " + e.getMessage());
							}
						}
					}
				} catch (Exception e) {
					System.err.println("⚠️ Read score error: " + e.getMessage());
				}

				try {
					redisTemplate.delete(RECOMMEND_FEED_CACHE_SCORE + userName);
				} catch (Exception e) {
					System.err.println("⚠️ Score delete error: " + e.getMessage());
				}
			}
		} catch (Exception e) {
			System.err.println("🔥 [onMessage] unexpected error: " + e.getMessage());
			e.printStackTrace();
		}
	}

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
			.map(Object::toString)
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