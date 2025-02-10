package com.myflavor.myflavor.domain.feed.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.comment.model.entity.Comment;
import com.myflavor.myflavor.domain.feed.DTO.service.VisitMethod;
import com.myflavor.myflavor.domain.heart.model.entity.Heart;

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
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Valid
	private String title;
	private String feedPhoto;

	@Enumerated(EnumType.STRING)
	private VisitMethod visitMethod;

	@Lob
	@Valid
	private String content;

	private Long restaurantId;

	@ColumnDefault("0")
	@Builder.Default
	private Integer heartCnt = 0;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Heart> haerts;

	// // TODO CASCADE 조건 확인.
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<FeedConfigration> configurations;

	@ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@ToString.Exclude
	private User user;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<SubFeed> subFeeds;

	@OneToMany
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<Comment> comments;

}
