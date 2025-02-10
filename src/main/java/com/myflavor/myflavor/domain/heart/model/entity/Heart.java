package com.myflavor.myflavor.domain.heart.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.comment.model.entity.Comment;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.heart.DTO.FavoriteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Heart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	private FavoriteType favoriteType;

	@ManyToOne
	private User user;
	@ManyToOne
	private MainFeed mainFeed;
	@ManyToOne
	private Comment comment;
}
