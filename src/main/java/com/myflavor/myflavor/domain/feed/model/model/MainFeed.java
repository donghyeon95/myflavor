package com.myflavor.myflavor.domain.feed.model.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import net.bytebuddy.asm.Advice;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.DTO.VisitMethod;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
	private Integer heartCnt;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Heart> haerts;

	// // TODO CASCADE 조건 확인.
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<FeedConfigration> configurations;

	@ManyToOne(cascade = CascadeType.REMOVE)
	private User user;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<SubFeed> subFeeds;

	@OneToMany
	@JoinColumn(name = "main_feed_id", referencedColumnName = "id")
	private List<Comment> comments;
}
