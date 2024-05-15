package com.myflavor.myflavor.domain.account.model.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
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

