package com.myflavor.myflavor.domain.account.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.myflavor.myflavor.domain.profile.model.entity.Follow;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String userEmail;

	private String password;

	private String name;

	private String phoneNumber;

	private String countryCallingCode;

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Follow> followers;

	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Follow> followings;

	@OneToMany
	@JoinColumn
	private List<Account> accounts = new ArrayList<>();
}

