package com.goode.service;

import com.goode.business.Account;
import com.goode.repository.AccountRepository;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService{

  @Autowired
  private AccountRepository accountRepository;


  public void savePerson(){
    //accountRepository.save(new Account(2, "teacher", "email", "password", 148820, 2, true, "first", "second", new Timestamp(new Date().getTime()), null));
  }
}
