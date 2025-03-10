package com.myflavor.myflavor.domain.account.model.repository;

import static com.myflavor.myflavor.domain.account.model.entity.QUser.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.myflavor.myflavor.domain.account.model.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements CustomUserRepository {

	@Autowired
	JPAQueryFactory jpaQueryFactory;

	public UserRepositoryImpl() {
		super(User.class);
	}

	@Override
	public List<User> search(Long id) {
		// JPQLQuery query = from(user);
		// query.where(user.id.eq(id));
		//
		// return query.fetchResults().getResults();

		return jpaQueryFactory.select(user).from(user)
			.where(user.id.eq(id))
			.fetch();
	}
}
