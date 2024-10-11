package com.myflavor.myflavor.domain.account.model.repository;

import com.myflavor.myflavor.domain.account.model.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	public Optional<Account> findByUserEmail(String userEmail);
}
