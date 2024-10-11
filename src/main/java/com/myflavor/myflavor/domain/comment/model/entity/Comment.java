package com.myflavor.myflavor.domain.comment.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.myflavor.myflavor.domain.feed.model.entity.MainFeed;
import com.myflavor.myflavor.domain.heart.model.entity.Heart;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@EntityListeners(AuditingEntityListener.class)
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Lob
	// FIXME 태그를 @문자로 파싱을 해서 쓸건데 => 이걸 비지니스 로직에서 처리를 하는 게 맞을 까?
	private String comment;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommentTag> tags;

	@ColumnDefault("0")
	@Builder.Default
	private Integer heartCnt = 0;

	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Heart> hearts;

	@ManyToOne
	private User user;

	@ManyToOne
	@JoinColumn(name = "main_feed_id")
	private MainFeed mainFeed;

	@ManyToOne
	@JoinColumn(name = "parent_comment_id")
	private Comment parentComment;
}
