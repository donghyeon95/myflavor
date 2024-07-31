package com.myflavor.myflavor.domain.account.model.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

	@OneToMany
	@JoinColumn
	private List<Account> accounts = new ArrayList<>();
}

