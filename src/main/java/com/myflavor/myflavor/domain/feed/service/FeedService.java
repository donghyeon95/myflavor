package com.myflavor.myflavor.domain.feed.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myflavor.myflavor.common.configuration.UID.snowflakeId.SnowFlakeIdProvider;
import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.mapper.FeedMapper;
import com.myflavor.myflavor.domain.feed.DTO.mapper.FeedResponseMapper;
import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.CustomPageResponse;
import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.MainFeedResponseDTO;
import com.myflavor.myflavor.domain.feed.model.entity.FeedConfigration;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;
import com.myflavor.myflavor.domain.picture.service.PictureService;
import com.myflavor.myflavor.domain.restaurant.model.entity.Restaurant;
import com.myflavor.myflavor.domain.restaurant.model.repository.RestaurantRepository;

@Service
public class FeedService implements MessageListener {

	private final String FEED_KEY_PREFIX = "feed:";
	private final String FEED_BACKUP_KEY_PREFIX = "backupFeed:";
	private final String USER_FEED_KEY_PREFIX = "user:";
	private final String USER_FEED_VIEW_LOG = "user:feed_view:";
	private MainFeedRepository mainFeedRepository;
	private RedisTemplate<String, Object> redisTemplate;
	private SnowFlakeIdProvider snowFlakeIdProvider;

	private PictureService pictureService;
	private UserRepository userRepository;
	private RestaurantRepository restaurantRepository;
	private FeedCandidateService feedCandidateService;
	private FeedCachManagerService feedCachManagerService;

	private final int TTL_SECONDS = 1800; // 30분
	private final double hot_weight = 0.8;
	private final double time_weight = 0.2;

	public FeedService(MainFeedRepository mainFeedRepository,
		RedisTemplate<String, Object> redisTemplate, SnowFlakeIdProvider snowFlakeIdProvider,
		FeedCandidateService feedCandidateService,
		FeedCachManagerService feedCachManagerService,
		PictureService pictureService, UserRepository userRepository,
		RestaurantRepository restaurantRepository) {
		this.mainFeedRepository = mainFeedRepository;
		this.redisTemplate = redisTemplate;
		this.snowFlakeIdProvider = snowFlakeIdProvider;
		this.feedCandidateService = feedCandidateService;
		this.feedCachManagerService = feedCachManagerService;
		this.pictureService = pictureService;
		this.userRepository = userRepository;
		this.restaurantRepository = restaurantRepository;
	}

	// FIXME  userNmae이 아니라 userVO나 User 객체를 받도록 수정.
	// FIXME update는 따로 Patch에서 만들도록 -> 지금은 어색한 로직이다.
	@Transactional
	public void insertUpdateFeed(long feedId, String userName, FeedResquestDTO feedHTTPVO) throws
		IOException,
		IllegalAccessException {
		// DB Insert 혹은 Update

		// DB 업데이트가 성공하면 redis temp 삭제
		// 1. user 정보를 받아온다
		User user = userRepository.findByName(userName).orElseThrow();
		System.out.println(feedHTTPVO);

		// 2. Feed의  데이터 저장
		// FeedHTTPVO => MainFeed, SubFeed 데이터 변경
		MainFeed mainFeed = mainFeedRepository.findById(feedId).orElse(null);

		// FIXME 사진 삭제 로직은 배치로 처리하는 것이 좋을 듯 (여기서 하기에 불필요한 로직)
		// picture 사진 폳더 구조 중에서 2개가 있는 데이터를 삭제
		if (mainFeed != null) {
			pictureService.deleteFile(mainFeed.getFeedPhoto(), userName);

			List<SubFeed> subFeeds = mainFeed.getSubFeeds();

			//  기존 SubFeed 및 관련 사진 삭제
			for (SubFeed subFeed : subFeeds) {
				pictureService.deleteFile(subFeed.getFeedPhoto(), userName); // 이미지 삭제
			}
		}

		FeedMapper.FeedEntities feedEntities = FeedMapper.feedMapper(feedId, feedHTTPVO, user);
		MainFeed newMainFeed = feedEntities.getMainFeed();
		Restaurant restaurant = restaurantRepository.findById(feedHTTPVO.getRestaurantId()).orElseThrow();
		newMainFeed.setRestaurant(restaurant);

		List<FeedConfigration> configrations = FeedMapper.feedConfigrationMapper(feedHTTPVO.getFeedSetting());
		configrations.stream().forEach(configration -> configration.setMainFeed(newMainFeed));
		newMainFeed.setConfigurations(configrations);
		mainFeedRepository.save(newMainFeed);

		// FIXME  만료 시간을 없애는 게 맞을 지  혹은 삭제=> 여러 key 삭제가 맞을 지 모르겠다
		// 이렇게 되면 저장한 데이터까지 삭제를 해버림.
		// redisTemplate.expire(this.FEED_KEY_PREFIX + feedId, -1, TimeUnit.SECONDS);

		// 임시 파일 삭제
		String key = this.FEED_KEY_PREFIX + feedId;
		System.out.println("redisFeedKey: " + key);
		deleteTempRedis(key);
	}

	// 상세 보기
	// FIXME REDIS 핫 랭크 업데이트 시 격리 조건  검토 하기
	public FeedResponseDTO getFeed(long feedId, String userName) {
		// feedId, userName
		// AUTH가 필요
		// 상세보기 한 내역을 저장할 필요가 있다.
		// user / feed / restaurant_category / 가게 위치 / feed_user / =>  상세 보기
		// Redis에 최근 본 피드 +
		MainFeed mainFeed = mainFeedRepository.findByIdToMainFeedDTO(feedId).orElseThrow();

		long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

		String redisCategoryKey =
			USER_FEED_VIEW_LOG + userName + ":category";
		String redisWriterKey = USER_FEED_VIEW_LOG + userName + ":writer";

		//FIXME Score 격리 조건 확인
		Double preCategoryScore = redisTemplate.opsForZSet()
			.score(redisCategoryKey, mainFeed.getRestaurant().getRestaurantCategory().getCategoryName());
		Double preWriterScore = redisTemplate.opsForZSet().score(redisWriterKey, mainFeed.getUser().getName());

		// 점수 기본값 설정 (없으면 0)
		if (preCategoryScore == null)
			preCategoryScore = 0.0;
		if (preWriterScore == null)
			preWriterScore = 0.0;

		// 새로운 점수 계산 (자주 본 항목 + 최근 본 항목 반영)
		double newCategoryScore = (preCategoryScore * 0.9) + (currentTime / 1000000.0);
		double newWriterScore = (preWriterScore * 0.9) + (currentTime / 1000000.0);

		// 피드 본 내역 로그 저장
		// 스코어는 이전에 있는 값을 가져와서 + 현재 시간을 더한다. => 자주 검색 + 최근에 검색
		// 카테고리 점수 업데이트 (전체 ZSET에 반영)
		redisTemplate.opsForZSet()
			.add(redisCategoryKey, mainFeed.getRestaurant().getRestaurantCategory().getCategoryName(),
				newCategoryScore);

		// 유저 점수 업데이트 (전체 ZSET에 반영)
		redisTemplate.opsForZSet().add(redisWriterKey, mainFeed.getUser().getName(), newWriterScore);

		return new FeedResponseDTO(new MainFeedDTO(mainFeed));
	}

	// FIXME Spring Cache를 사용하도록.
	public CustomPageResponse<MainFeedResponseDTO> getFeedList(String userName, Pageable pageable) {
		// AUTH 검증이 필요

		// MainFeed에서 특정 데이터만 가져올거임.
		// TODO 내가 1시간 이내 본 것은 후순위로 가도록 => bloom filter를 사용하거나, radis를 이용하여 체크 ()
		// TODO 이걸 연관된 Feed 추천 알고리즘으로 변경
		// 최근 1시간 이내 써진 글 => 없으면 1시간 씩 추가

		/** Cache update
		 *  cache miss 일 경우 => 기본 로직 (1주일 이후)
		 *  cache hit 일 경우 => 캐시 시간 이후
		 * */
		LocalDateTime latestFeedTime = feedCachManagerService.getLatestCacheTime(userName);

		// FIXME 만약에 시간이 1분 아래라면 db 안가도 될듯.
		Set<MainFeed> candidates = feedCandidateService.getCandidates(userName, latestFeedTime);

		// TODO 평가 함수
		// score 평가 => score는 지금은 시간 순으로 지정
		List<MainFeedDTO> candidateList = new ArrayList<>(candidates).stream().map(MainFeedDTO::new).toList();
		List<Long> scores = candidateList.stream().map(this::evaluationFunction).toList();
		feedCachManagerService.saveBulkFeedCache(userName, candidateList, scores);

		long pageOffset = pageable.getOffset();
		long pageSize = pageable.getPageSize();
		List<String> feedIds = feedCachManagerService.getFeedIds(userName, pageOffset, pageSize);

		// TODO  필터 함수
		List<MainFeedDTO> mainFeeds = feedCachManagerService.getMainFeedsFromIds(userName, feedIds, MainFeedDTO.class)
			.stream()
			.filter(this::filterFeedFunction)
			.toList();

		List<MainFeedResponseDTO> mainFeedResponseDTO = mainFeeds.stream()
			.filter(Objects::nonNull)
			.map(new FeedResponseMapper()::ToMainFeedResponseDTO).toList();

		return new CustomPageResponse<>(mainFeedResponseDTO, pageable, feedIds.size());

	}

	/** 평가 함수
	 * 향후 개인화된 추천 점수 도출
	 * */
	public long evaluationFunction(MainFeedDTO mainFeedDTO) {
		return mainFeedDTO.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
	}

	/**
	 * 필터 함수
	 * @return
	 */
	public boolean filterFeedFunction(MainFeedDTO mainFeedDTO) {
		// 유저 설정 정보
		// 비공개 여부 등
		// 유해 게시글 여부

		return true;
	}

	public long generateId() {
		return snowFlakeIdProvider.nextId();
	}

	public void insertTempFeed(long feedId, FeedResquestDTO feedHTTPVO, String userName) {
		// redis에 임시 파일을 저장

		/* 1. redis 임시 파일 저장
			redis 키 => feedId
			hashKey => Main(0), Sub+priority
			=> 일단 위 방식이 아닌 한번에 가도록
		 */
		String feedKey = this.FEED_KEY_PREFIX + feedId;
		redisTemplate.opsForValue().set(feedKey, feedHTTPVO, 10, TimeUnit.MINUTES);

		/*
			user feed 목록에 데이터 저장
		 */
		String userKey = this.USER_FEED_KEY_PREFIX + userName;
		redisTemplate.opsForSet().add(userKey, feedKey);

		// 백업용 데이터
		String feedBackupKey = this.FEED_BACKUP_KEY_PREFIX + feedKey;
		redisTemplate.opsForValue().set(feedBackupKey, feedHTTPVO);
	}

	public Set<Object> getTempFeedIds(String userName) {
		String redisKey = this.USER_FEED_KEY_PREFIX + userName;
		return redisTemplate.opsForSet().members(redisKey);
	}

	public FeedResquestDTO getTempFeed(long feedId, String userName) {
		Object feedObject = redisTemplate.opsForValue().get(this.FEED_KEY_PREFIX + feedId);
		FeedResquestDTO feed = new ObjectMapper().convertValue(feedObject, FeedResquestDTO.class);

		System.out.println(feed);
		// 권한 확인
		return null;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();
		if (expiredKey.startsWith(this.FEED_KEY_PREFIX)) {
			try {
				this.handleFeedExpiration(expiredKey);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void handleFeedExpiration(String key) throws IOException, IllegalAccessException {
		String backupKey = this.FEED_BACKUP_KEY_PREFIX + key;
		FeedResquestDTO feedData = (FeedResquestDTO)redisTemplate.opsForValue().get(backupKey);

		System.out.println(feedData);
		if (feedData == null)
			return;
		String userName = null;
		try {
			redisTemplate.delete(backupKey);
			FeedDTO[] feedDTOS = Objects.requireNonNull(feedData).getFeed();

			for (FeedDTO feedDTO : feedDTOS) {
				String picturePath = feedDTO.getPicturePath();
				if (picturePath == null)
					throw new IllegalArgumentException("Path is nessesary");

				String[] splitedPath = picturePath.split("/");

				if (splitedPath.length < 4)
					throw new IllegalArgumentException("Path is not valid");

				// FIXME 의존성 수정
				String user = splitedPath[splitedPath.length - 4];
				System.out.println("user: " + user);
				userName = user;

				redisTemplate.delete(picturePath);
				pictureService.deleteFile(picturePath, user);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// user data 제거
			redisTemplate.opsForSet().remove(this.USER_FEED_KEY_PREFIX + userName, key);
			if (redisTemplate.opsForSet().members(this.USER_FEED_KEY_PREFIX + userName).isEmpty()) {
				redisTemplate.delete(this.USER_FEED_KEY_PREFIX + userName);
			}
		}
	}

	public void deleteTempRedis(String key) {
		String backupKey = this.FEED_BACKUP_KEY_PREFIX + key;
		FeedResquestDTO feedData = (FeedResquestDTO)redisTemplate.opsForValue().get(backupKey);

		System.out.println(feedData);
		String userName = null;
		try {
			redisTemplate.delete(key);
			redisTemplate.delete(backupKey);
			if (feedData == null)
				return;
			FeedDTO[] feedDTOS = Objects.requireNonNull(feedData).getFeed();

			for (FeedDTO feedDTO : feedDTOS) {
				String picturePath = feedDTO.getPicturePath();
				if (picturePath == null)
					throw new IllegalArgumentException("Path is nessesary");

				String[] splitedPath = picturePath.split("/");

				if (splitedPath.length < 4)
					throw new IllegalArgumentException("Path is not valid");

				// FIXME 의존성 수정
				String user = splitedPath[splitedPath.length - 4];
				System.out.println("user: " + user);
				userName = user;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			// user data 제거
			redisTemplate.opsForSet().remove(this.USER_FEED_KEY_PREFIX + userName, key);
			if (redisTemplate.opsForSet().members(this.USER_FEED_KEY_PREFIX + userName).isEmpty()) {
				redisTemplate.delete(this.USER_FEED_KEY_PREFIX + userName);
			}
		}
	}
}
