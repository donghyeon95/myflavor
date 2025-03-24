package com.myflavor.myflavor.domain.feed.model.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.comment.model.entity.Comment;
import com.myflavor.myflavor.domain.feed.DTO.mapper.LocalDateTimeToStringSerializer;
import com.myflavor.myflavor.domain.feed.DTO.mapper.StringToLocalDateTimeDeserializer;
import com.myflavor.myflavor.domain.feed.DTO.service.VisitMethod;
import com.myflavor.myflavor.domain.heart.model.entity.Heart;
import com.myflavor.myflavor.domain.restaurant.model.entity.Restaurant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MainFeed {
	@Id
	private Long id;

	@CreatedDate
	@Column(updatable = false)
	@JsonSerialize(using = LocalDateTimeToStringSerializer.class)
	@JsonDeserialize(using = StringToLocalDateTimeDeserializer.class)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@JsonSerialize(using = LocalDateTimeToStringSerializer.class)
	@JsonDeserialize(using = StringToLocalDateTimeDeserializer.class)
	private LocalDateTime updatedAt;

	@Valid
	private String title;
	private String feedPhoto;

	@Enumerated(EnumType.STRING)
	private VisitMethod visitMethod;

	@Lob
	@Valid
	private String content;

	@ColumnDefault("0")
	@Builder.Default
	private Integer heartCnt = 0;

	@OneToMany(mappedBy = "mainFeed", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Heart> haerts;

	@ManyToOne
	@JoinColumn(unique = false)
	private Restaurant restaurant;

	//
	@OneToMany(mappedBy = "mainFeed", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FeedConfigration> configurations;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	@JsonIgnore
	private User user;

	@OneToMany(mappedBy = "mainFeed", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<SubFeed> subFeeds;

	@OneToMany(mappedBy = "mainFeed", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Comment> comments;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		MainFeed mainFeed = (MainFeed)o;
		return Objects.equals(id, mainFeed.id);  // 'id'가 같으면 같은 객체로 취급
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);  // 'id'를 기준으로 해시코드 생성
	}
}
