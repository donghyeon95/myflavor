package com.myflavor.myflavor.domain.feed.model.model;

import com.myflavor.myflavor.domain.account.model.model.User;
import com.myflavor.myflavor.domain.feed.model.model.Comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentTag {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	private Comment comment;

	@ManyToOne
	private User user;
}
