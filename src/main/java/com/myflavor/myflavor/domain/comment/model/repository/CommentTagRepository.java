package com.myflavor.myflavor.domain.comment.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.comment.model.entity.CommentTag;

public interface CommentTagRepository extends JpaRepository<CommentTag, Long> {
}
