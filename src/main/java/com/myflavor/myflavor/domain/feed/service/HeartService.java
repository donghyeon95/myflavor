package com.myflavor.myflavor.domain.feed.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
import com.myflavor.myflavor.domain.feed.model.model.Heart;
import com.myflavor.myflavor.domain.feed.model.model.MainFeed;
import com.myflavor.myflavor.domain.feed.model.repository.CommentRepository;
import com.myflavor.myflavor.domain.feed.model.repository.HeartRepository;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;

@Service
public class HeartService {

	private final String REDIS_FEED_HEART_KEY = "haert:feed:";
	private final String REDIS_COMMENT_HEART_KEY = "haert:comment:";

	private HeartRepository heartRepository;
	private CommentRepository commentRepository;
	private MainFeedRepository mainFeedRepository;
	private UserRepository userRepository;
	private RedisTemplate<String, Object> redisTemplate;

	public HeartService(CommentRepository commentRepository, MainFeedRepository mainFeedRepository,
			UserRepository userRepository, HeartRepository heartRepository, RedisTemplate<String, Object> redisTemplate) {
		this.commentRepository = commentRepository;
		this.mainFeedRepository = mainFeedRepository;
		this.userRepository = userRepository;
		this.heartRepository = heartRepository;
		this.redisTemplate = redisTemplate;
	}

	@Transactional
	public void upFeedHeart(long feedId, String userName) {
		// 1. 이미 좋아요를 한 유저인 지 확인
		MainFeed mainFeed = mainFeedRepository.findById(feedId).orElseThrow();
		User user = userRepository.findByName(userName).orElseThrow();
		// 2.1 좋아요를 했다면 return// 3.2  저장에 실패 하면 ERROR 처리
		if (heartRepository.existsByMainFeedAndUser(mainFeed, user))
			return;

		// 2.2 좋아요가 없다면 RDB (feed - heart - user) => heart에 저장
		Heart heart = Heart.builder()
				.mainFeed(mainFeed)
				.user(user)
				.build();

		try {
			heartRepository.save(heart); // heart 저장
			// => 이거 저장할 때, 동시에 2개가 저장이 되는 경우라면 1개만 적용이 되는 건가?
		} catch (DataIntegrityViolationException e) {
			// 고유 제약 조건 위반 발생 시 이미 좋아요가 존재하는 경우로 간주하고 무시
			System.out.println("Duplicate like detected. Ignoring the request.");
			return;
		} catch (Exception e) {
			// 3.2  저장에 실패 하면 ERROR 처리
			System.out.println("Error occurred while saving the heart: " + e.getMessage());
		}

		// 3.1 저장이 성공하면 redis에 좋아요 수 반영
		String redisHeartCntKey = this.REDIS_FEED_HEART_KEY + feedId;
		Object redisObject = redisTemplate.opsForValue().get(redisHeartCntKey);
		Long heartCnt = redisTemplate.opsForValue().increment(redisHeartCntKey);

		// Redis에 heart count가 없다면 RDB에서 가져와서 Redis에 저장
		if (heartCnt == 1) {
			long heartCount = heartRepository.countByMainFeed(mainFeed);
			redisTemplate.opsForValue().set(redisHeartCntKey, heartCount);
		}

		// 4. 주기적으로 redis의 값을 rdb에 업데이트
		// 이때, 데이터가 많으면 어떻게 하지?

		// 5. 주기적으로 heart 디비를 count 하여 heart cnt 업데이트
		// 이때, 데이터가 많으면 어떻게 하지??
	}

}
