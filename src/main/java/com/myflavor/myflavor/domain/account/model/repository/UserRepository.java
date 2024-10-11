package com.myflavor.myflavor.domain.account.model.repository;

import com.myflavor.myflavor.domain.account.model.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByUserEmail(String userEmail);

	public Optional<User> findByName(String userName);
}