package com.myflavor.myflavor.domain.feed.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.feed.model.model.Heart;

public interface HeartRepository extends JpaRepository<Heart, Long> {
}
