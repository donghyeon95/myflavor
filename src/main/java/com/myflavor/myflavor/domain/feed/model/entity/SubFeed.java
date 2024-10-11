package com.myflavor.myflavor.domain.feed.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// create관련 참고 https://blog.naver.com/seek316/223355950299
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SubFeed {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	// TODO 공통 Entity로 분리
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	private Integer priority;
	private String feedPhoto;
	private String content;

	// FIXME 이런식으로 Join을 걸어 주는 것이 맞냐? 아니면 그냥 Id 값을 가지고 있는 것이 맞냐?
	@ManyToOne(cascade = CascadeType.PERSIST)
	private MainFeed mainFeedId;
}
