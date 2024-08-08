package com.myflavor.myflavor.domain.feed.model.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.DTO.FavoriteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
