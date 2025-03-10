package com.myflavor.myflavor.domain.account.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.myflavor.myflavor.domain.account.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

	public Optional<User> findByUserEmail(String userEmail);

	public Optional<User> findByName(String userName);
}