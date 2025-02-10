package com.myflavor.myflavor.domain.feed.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myflavor.myflavor.domain.feed.model.entity.FeedConfigration;

public interface FeedConfigurationRepository extends JpaRepository<FeedConfigration, Long> {
}