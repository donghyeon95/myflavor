package com.myflavor.myflavor.domain.feed.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.repository.MainFeedRepository;

@Service
public class FeedCandidateService {
	private MainFeedRepository mainFeedRepository;
	private FeedViewLogService feedViewLogService;

	public FeedCandidateService(MainFeedRepository mainFeedRepository, FeedViewLogService feedViewLogService) {
		this.mainFeedRepository = mainFeedRepository;
		this.feedViewLogService = feedViewLogService;
	}

	/**
	 * Feed 후보를 가져온다.
	 */
	public Set<MainFeed> getCandidates(String userName, LocalDateTime latestFeedTime) {
		//후보군 1000개
		// FIXME 이걸 매번 쿼리를 하는 것이 아닌 일정 스코어까지는 사용
		// FIXME -> (해당 리스트의 일정이상이 본 것이거나, hot record가 내려갔다면... ( 해당 후보군의 평균 생성시간, 현재 트랜드(전체 검색 통걔), 내 검색/view 통계 등과 맞는 지 점수를 검증한다. )  ) )
		// 각 데이터를 가져오는 데 특정 함수를 반복해서 가져올 수 있도록 한다.
		// 어떤 디자인 패턴으로 구현하는 것이 좋을 까?
		Set<MainFeed> candidates = new HashSet<>();

		// TODO 비동기 => 코루틴()
		// In-Network (900)개
		candidates.addAll(getFrequentCategoryFeeds(userName,
			latestFeedTime));  // 내가 자주 본 글 (상세 보기 기준) => 그 글의 가게 카테고리랑 10개 && 인기글 && 최신 (300개)
		candidates.addAll(getFrequentUserFeeds(userName,
			latestFeedTime)); // 내가 자주 본 사람 (상세를 본 기준) -> 최근에 자주 본 카테고리 10개 && 전체 인기글 && 최신 (300개)
		candidates.addAll(getFollowFeeds(userName, latestFeedTime)); // 내가 팔로우한 사람 기준으로 => 인기글 && 최신 (300개)
		// TODO 검색한 내용을 기준으로
		/**
		 * TODO Out-Network (100)개
		 * 인기 있는 글 위주로 (50개)
		 * 광고글 (10개)
		 * 내용(핫한 컨텐츠 + 날씨별 예측) => 코사인 유사도 측정해도 되고... -> elastic (40개)
		 */

		return candidates;
	}

	/**
	 * 특정 시간 이후 내가 많이 본 카테고리 유저 데이터 가져오기
	 */
	public List<MainFeed> getFrequentCategoryFeeds(String userName, LocalDateTime latestFeedTime) {
		List<String> frequentCategories = feedViewLogService.getFrequentViewCategory(userName, 10);
		return mainFeedRepository.findByRestaurant_RestaurantCategory_CategoryNameInAndCreatedAtAfterOrderByCreatedAtDesc(
			frequentCategories, latestFeedTime);
	}

	/**
	 * 내가 많이 본 피드 유저 데이터 가져오기
	 */
	public List<MainFeed> getFrequentUserFeeds(String userName, LocalDateTime latestFeedTime) {
		List<String> frequentViewPersons = feedViewLogService.getFrequentViewWriter(userName, 10);
		return mainFeedRepository.findByUser_NameInAndCreatedAtAfterOrderByCreatedAtDesc(
			frequentViewPersons, latestFeedTime);
	}

	/**
	 * 내가 팔로우한 유저 카테고리 데이터 가져오기
	 */
	public List<MainFeed> getFollowFeeds(String userName, LocalDateTime latestFeedTime) {
		return mainFeedRepository.queryByUserFollower(userName, latestFeedTime);
	}

}
