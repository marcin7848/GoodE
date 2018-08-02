package com.goode.service;

import com.goode.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService{

  @Autowired
  private AccountRepository accountRepository;

}
