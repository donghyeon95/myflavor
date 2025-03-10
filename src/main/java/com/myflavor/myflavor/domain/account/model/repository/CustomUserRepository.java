package com.myflavor.myflavor.domain.account.model.repository;

import java.util.List;

import com.myflavor.myflavor.domain.account.model.entity.User;

public interface CustomUserRepository {
	public List<User> search(Long id);
}
