package com.goode.service;

import com.goode.SendEmail;
import com.goode.business.Account;
import com.goode.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService, StandardizeService<Account>{

  @Autowired
  private SendEmail sendEmail;

  @Autowired
  private AccountRepository accountRepository;

  @Override
  public ResponseEntity<?> addNew(Account entity){
    //AccessRole accessRole = new AccessRole();
    //accessRole.setId(2);
    //Account account = accountRepository.save(new Account(2, "teacher", "email", "password", 148820, accessRole, true, "first", "second", new Timestamp(new Date().getTime()), null, null));

    sendEmail.send("marcin7848@gmail.com", "Rejestracja TODOList", "To activate your account use this code: \n");



    return new ResponseEntity<>(entity, HttpStatus.OK);
  }
}
