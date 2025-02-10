package com.myflavor.myflavor.domain.feed.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

@Service
public class FeedService implements MessageListener {

	private final String FEED_KEY_PREFIX = "feed:";
	private final String FEED_BACKUP_KEY_PREFIX = "backupFeed:";
	private final String USER_FEED_KEY_PREFIX = "user:";
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
	@Transactional
	public void insertUpdateFeed(long feedId, String userName, FeedResquestDTO feedHTTPVO) {
		// DB Insert 혹은 Update

		// DB 업데이트가 성공하면 redis temp 삭제
		// 1. user 정보를 받아온다
		User user = userRepository.findByName(userName).orElseThrow();
		System.out.println(feedHTTPVO);

		// 2. Feed의  데이터 저장
		// FeedHTTPVO => MainFeed, SubFeed 데이터 변경
		FeedMapper.FeedEntities feedEntities = FeedMapper.feedMapper(feedId, feedHTTPVO, user);
		MainFeed mainFeed = feedEntities.getMainFeed();
		List<SubFeed> subFeeds = feedEntities.getSubFeeds();
		mainFeedRepository.save(mainFeed);
		subFeedRepository.saveAll(subFeeds);

		List<FeedConfigration> configrations = FeedMapper.feedConfigrationMapper(feedHTTPVO.getFeedSetting());
		feedConfigurationRepository.saveAll(configrations);

		// FIXME  만료 시간을 없애는 게 맞을 지  혹은 삭제=> 여러 key 삭제가 맞을 지 모르겠다
		// 이렇게 되면 저장한 데이터까지 삭제를 해버림.
		// redisTemplate.expire(this.FEED_KEY_PREFIX + feedId, -1, TimeUnit.SECONDS);
		String key = this.FEED_KEY_PREFIX + feedId;
		System.out.println("redisFeedKey: " + key);
		deleteTempRedis(key);
	}

	public FeedResponseDTO getFeed(long feedId, String userName) {
		// feedId, userName
		// AUTH가 필요
		return mainFeedRepository.findByIdToMainFeedDTO(feedId)
			.map(MainFeedDTO::new)
			.map(FeedResponseDTO::new)
			.orElseThrow();
	}

	public CustomPageResponse<MainFeedResponseDTO> getFeedList(String userName, Pageable pageable) {
		// AUTH 검증이 필요

		// MainFeed에서 특정 데이터만 가져올거임.
		// TODO 이걸 연관된 Feed 추천 알고리즘으로 변경
		Page<MainFeedDTO> mainFeed = mainFeedRepository.findByUserName(userName, pageable);
		List<MainFeedResponseDTO> mainFeedResponseDTO = mainFeed.stream()
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
