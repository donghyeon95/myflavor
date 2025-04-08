package com.myflavor.myflavor.domain.feed.service;

import java.util.List;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.feed.DTO.db.FeedDTO;
import com.myflavor.myflavor.domain.feed.DTO.request.FeedResquestDTO;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.feed.model.entity.SubFeed;
import com.myflavor.myflavor.domain.restaurant.model.entity.Restaurant;
import com.myflavor.myflavor.domain.restaurant.model.entity.RestaurantCategory;

public class TestMockFactory {
	public static FeedResquestDTO createFeedRequestDTO() {
		return FeedResquestDTO.builder()
			.title("하이네 오늘 저녁은?")
			.feed(new FeedDTO[] {
				new FeedDTO("먼저 이건 초밥 세트",
					"http://localhost:8080/picture/download/ine/1815017301389479936/0/세돌세돌_.jpg"),
				new FeedDTO("먼저 이건 초밥 세트",
					"http://localhost:8080/picture/download/ine/1815017301389479936/1/세돌세돌_.jpg"),
				new FeedDTO("먼저 이건 초밥 세트", "http://localhost:8080/picture/download/ine/1815017301389479936/2/세돌세돌_.jpg")
			})
			.feedSetting(null)
			.restaurantId(1223107)
			.userName("ine")
			.build();
	}

	public static MainFeed createMainFeed() {
		MainFeed feed = new MainFeed();
		feed.setFeedPhoto("uploads/testUser/main/image.jpg");

		SubFeed sub = new SubFeed();
		sub.setFeedPhoto("uploads/testUser/sub/image.jpg");

		feed.setSubFeeds(List.of(sub));

		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantCategory(new RestaurantCategory());
		feed.setRestaurant(restaurant);

		User user = new User();
		user.setName("testUser");
		feed.setUser(user);

		return feed;
	}

	public static User createUser() {
		return User.builder()
			.name("ine")
			.build();
	}

}
