// package com.myflavor.myflavor.domain.feed.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.NoSuchElementException;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.SetOperations;
// import org.springframework.data.redis.core.ValueOperations;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.myflavor.myflavor.common.configuration.UID.snowflakeId.SnowFlakeIdProvider;
// import com.myflavor.myflavor.domain.account.model.entity.User;
// import com.myflavor.myflavor.domain.account.model.repository.UserRepository;
// import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
// import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
// import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;
// import com.myflavor.myflavor.domain.picture.service.PictureService;
// import com.myflavor.myflavor.domain.restaurant.model.entity.Restaurant;
// import com.myflavor.myflavor.domain.restaurant.model.repository.RestaurantRepository;
//
// @Transactional
// // @SpringBootTest
// @ExtendWith(MockitoExtension.class)
// public class FeedServiceTest {
//
// 	@Mock
// 	private MainFeedRepository mainFeedRepository;
// 	@Mock
// 	private RedisTemplate<String, Object> redisTemplate;
// 	@Mock
// 	private ValueOperations<String, Object> valueOperations;
// 	@Mock
// 	private SetOperations<String, Object> setOperations;
// 	@Mock
// 	private PictureService pictureService;
// 	@Mock
// 	private UserRepository userRepository;
// 	@Mock
// 	private RestaurantRepository restaurantRepository;
// 	@Mock
// 	private SnowFlakeIdProvider snowFlakeIdProvider;
// 	@Mock
// 	private FeedCandidateService feedCandidateService;
// 	@Mock
// 	private FeedCachManagerService feedCachManagerService;
//
// 	@InjectMocks
// 	// @Autowired
// 	private FeedService feedService;
//
// 	private final String userName = "donghyeon";
// 	private final long feedId = 1901598210552631296L;
// 	private FeedResquestDTO resquestDTO;
//
// 	// @BeforeEach
// 	// void checkInjection() {
// 	// 	// assertNotNull(feedService);
// 	// }
//
// 	@BeforeEach
// 	public void setUP() {
// 		System.out.println("===========================");
// 		System.out.println("Feed Service Test is start");
// 		System.out.flush(); // ⬅️ 강제 flush
// 		resquestDTO = TestMockFactory.createFeedRequestDTO();
// 		System.out.println(resquestDTO);
// 	}
//
// 	@Test
// 	@DisplayName("피드 추가 성공")
// 	public void testInsertUpdateFeed_success() throws Exception {
// 		System.out.println("=== 테스트 실행됨 ===");
// 		// Given: 주어진 상황 설정 (보통 함수의 파라미터나 전제 조건 설정)
// 		User user = TestMockFactory.createUser(); //
// 		MainFeed mainFeed = TestMockFactory.createMainFeed();
// 		Restaurant restaurant = Restaurant.builder().build();
//
// 		given(redisTemplate.opsForValue()).willReturn(valueOperations);
// 		given(redisTemplate.opsForSet()).willReturn(setOperations);
// 		given(userRepository.findByName(anyString())).willReturn(Optional.of(user));
// 		given(mainFeedRepository.findById(any())).willReturn(Optional.of(mainFeed));
// 		given(restaurantRepository.findById(any())).willReturn(Optional.of(restaurant));
//
// 		// When: 테스트할 기능을 호출하고 실행하는 부분
// 		feedService.insertUpdateFeed(feedId, userName, resquestDTO);
// 		//
// 		// Then: 기대한 결과를 검증.
// 		verify(pictureService, times(2)).deleteFile(anyString(), eq(userName));
// 		verify(mainFeedRepository).save(any(MainFeed.class));
// 	}
//
// 	@Test
// 	@DisplayName("피드 추가 실패 - 유저 없음")
// 	public void insertFeed_fail_userNotFound() throws Exception {
// 		// given
// 		given(userRepository.findByName(anyString())).willReturn(Optional.empty());
//
// 		// when
// 		assertThrows(NoSuchElementException.class,
// 			() -> feedService.insertUpdateFeed(feedId, userName, resquestDTO));
// 	}
//
// 	@Test
// 	@DisplayName("이전 사진 데이터 삭제 성공")
// 	public void deletePicture_success() throws Exception {
// 		// given
// 		User user = TestMockFactory.createUser();
// 		MainFeed mainFeed = TestMockFactory.createMainFeed();
// 		Restaurant restaurant = Restaurant.builder().build();
//
// 		given(userRepository.findByName(anyString())).willReturn(Optional.of(user));
// 		given(mainFeedRepository.findById(anyLong())).willReturn(Optional.of(mainFeed));
// 		given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(restaurant));
//
// 		// redis ops stub
// 		given(redisTemplate.opsForValue()).willReturn(valueOperations);
// 		given(redisTemplate.opsForSet()).willReturn(setOperations);
//
// 		// when
// 		feedService.insertUpdateFeed(feedId, userName, resquestDTO);
//
// 		// then
// 		verify(pictureService, times(2)).deleteFile(anyString(), eq(userName));
// 	}
//
// 	@Test
// 	@DisplayName("Redis 임시 피드 삭제 성공")
// 	public void deleteTempFeed_success() throws Exception {
// 	}
//
// }
