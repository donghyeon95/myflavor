package com.myflavor.myflavor.domain.feed.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.myflavor.myflavor.domain.comment.model.repository.CommentRepository;
import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.db.MainFeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.mapper.FeedMapper;
import com.myflavor.myflavor.domain.feed.DTO.mapper.FeedResponseMapper;
import com.myflavor.myflavor.domain.feed.DTO.redis.FeedViewLogDTO;
import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.CustomPageResponse;
import com.myflavor.myflavor.domain.feed.DTO.response.FeedResponseDTO;
import com.myflavor.myflavor.domain.feed.DTO.response.MainFeedResponseDTO;
import com.myflavor.myflavor.domain.feed.model.entity.FeedConfigration;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;
import com.myflavor.myflavor.domain.feed.model.repository.FeedConfigurationRepository;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;
import com.myflavor.myflavor.domain.feed.model.repository.SubFeedRepository;
import com.myflavor.myflavor.domain.heart.model.repository.HeartRepository;
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
	private SubFeedRepository subFeedRepository;
	private CommentRepository commentRepository;
	private FeedConfigurationRepository feedConfigurationRepository;
	private HeartRepository heartRepository;
	private RedisTemplate<String, Object> redisTemplate;
	private SnowFlakeIdProvider snowFlakeIdProvider;
	private ObjectMapper objectMapper;
	@Autowired
	private PictureService pictureService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RestaurantRepository restaurantRepository;
	private final int TTL_SECONDS = 1800; // 30분
	private final double hot_weight = 0.8;
	private final double time_weight = 0.2;

	public FeedService(MainFeedRepository mainFeedRepository, SubFeedRepository subFeedRepository,
		CommentRepository commentRepository,
		FeedConfigurationRepository feedConfigurationRepository, HeartRepository heartRepository,
		RedisTemplate<String, Object> redisTemplate, SnowFlakeIdProvider snowFlakeIdProvider,
		ObjectMapper objectMapper) {
		this.mainFeedRepository = mainFeedRepository;
		this.subFeedRepository = subFeedRepository;
		this.commentRepository = commentRepository;
		this.feedConfigurationRepository = feedConfigurationRepository;
		this.heartRepository = heartRepository;
		this.redisTemplate = redisTemplate;
		this.snowFlakeIdProvider = snowFlakeIdProvider;
		this.objectMapper = objectMapper;
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
		FeedViewLogDTO feedViewLogDTO = FeedViewLogDTO.builder()
			.feedWriterName(mainFeed.getUser().getName())
			.feedId(mainFeed.getId())
			.latitude(mainFeed.getRestaurant().getLatitude())
			.longitude(mainFeed.getRestaurant().getLongitude())
			.viewerName(userName)
			.restaurantCategory(mainFeed.getRestaurant().getRestaurantCategory().getCategoryName())
			.build();

		Double preCategoryScore = redisTemplate.opsForZSet()
			.score(redisCategoryKey, mainFeed.getRestaurant().getRestaurantCategory().getCategoryName());
		Double preWriterScore = redisTemplate.opsForZSet().score(redisWriterKey, mainFeed.getUser().getName());

		// 점수 기본값 설정 (없으면 0)
		if (preCategoryScore == null)
			preCategoryScore = 0.0;
		if (preWriterScore == null)
			preWriterScore = 0.0;

		// 🔥 새로운 점수 계산 (자주 본 항목 + 최근 본 항목 반영)
		double newCategoryScore = (preCategoryScore * 0.9) + (currentTime / 1000000.0);
		double newWriterScore = (preWriterScore * 0.9) + (currentTime / 1000000.0);

		// 피드 본 내역 로그 저장
		// 스코어는 이전에 있는 값을 가져와서 + 현재 시간을 더한다. => 자주 검색 + 최근에 검색
		// 🔥 카테고리 점수 업데이트 (전체 ZSET에 반영)
		redisTemplate.opsForZSet()
			.add(redisCategoryKey, mainFeed.getRestaurant().getRestaurantCategory().getCategoryName(),
				newCategoryScore);

		// 🔥 유저 점수 업데이트 (전체 ZSET에 반영)
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

		long pageOffset = pageable.getOffset();
		long pageSize = pageable.getPageSize();

		/** Cache update
		 *  cache miss 일 경우 => 기본 로직 (1주일 이후)
		 *  cache hit 일 경우 => 캐시 시간 이후
		 * */

		// 후보군 1000개
		// FIXME 이걸 매번 쿼리를 하는 것이 아닌 일정 스코어까지는 사용
		// FIXME -> (해당 리스트의 일정이상이 본 것이거나, hot record가 내려갔다면... ( 해당 후보군의 평균 생성시간, 현재 트랜드(전체 검색 통걔), 내 검색/view 통계 등과 맞는 지 점수를 검증한다. )  ) )
		Set<MainFeed> candidates = new HashSet<>();

		// TODO 비동기 => 코루틴()
		// In-Network (900)개
		// 내가 팔로우한 사람 기준으로 => 인기글 && 최신 (300개)
		List<MainFeed> followerFeeds = mainFeedRepository.queryByUserFollower(userName,
			LocalDateTime.now().minusDays(8));
		candidates.addAll(followerFeeds);

		// 내가 자주 본 사람 (상세를 본 기준) -> 최근에 자주 본 카테고리 10개 && 전체 인기글 && 최신 (300개)
		String frequentPersonkey = USER_FEED_VIEW_LOG + userName + ":writer";
		List<String> frequentViewPersons = Objects.requireNonNull(redisTemplate.opsForZSet().reverseRange(
				frequentPersonkey, 0, 9))
			.stream().map(String::valueOf).collect(
				Collectors.toList());
		List<MainFeed> frequentPersionFeeds = mainFeedRepository.findByUser_NameInOrderByCreatedAtDesc(
			frequentViewPersons);
		candidates.addAll(frequentPersionFeeds);

		// 내가 자주 본 글 (상세 보기 기준) => 그 글의 가게 카테고리랑 10개 && 인기글 && 최신 (300개)
		// TODO 검색한 내용을 기준으로
		String frequentCategoryKey = USER_FEED_VIEW_LOG + userName + ":category";
		List<String> frequentCategories = Objects.requireNonNull(
				redisTemplate.opsForZSet().reverseRange(frequentCategoryKey, 0, 9))
			.stream().map(String::valueOf).collect(Collectors.toList());
		List<MainFeed> frequentCategoryFeeds = mainFeedRepository.findByRestaurant_RestaurantCategory_CategoryNameInOrderByCreatedAtDesc(
			frequentCategories);
		candidates.addAll(frequentCategoryFeeds);

		/**
		 * TODO Out-Network (100)개
		 * 인기 있는 글 위주로 (50개)
		 * 광고글 (10개)
		 * 내용(핫한 컨텐츠 + 날씨별 예측) => 코사인 유사도 측정해도 되고... -> elastic (40개)
		 */

		Page<MainFeedDTO> mainFeed = mainFeedRepository.findByUser_Name(userName, pageable).map(MainFeedDTO::new);
		List<MainFeedResponseDTO> mainFeedResponseDTO = candidates.stream()
			.map(MainFeedDTO::new)
			.filter(Objects::nonNull)
			.map(new FeedResponseMapper()::ToMainFeedResponseDTO).toList();

		return new CustomPageResponse<>(mainFeedResponseDTO, pageable, mainFeed.getTotalElements());
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
		System.out.println("Message: " + message);
		String expiredKey = message.toString();
		System.out.println("expiredKey: " + expiredKey);

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
