package com.goode.service;

import com.goode.Language;
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
  public ResponseEntity<?> addNew(Account account){
    //AccessRole accessRole = new AccessRole();
    //accessRole.setId(2);
    //Account account = accountRepository.save(new Account(2, "teacher", "email", "password", 148820, accessRole, true, "first", "second", new Timestamp(new Date().getTime()), null, null));

    if(validateAccount(account).getStatusCode() != HttpStatus.OK)
      return validateAccount(account);

    //sendEmail.send("marcin7848@gmail.com", Language.REGISTRATION_GOODE.getString(), "To activate your account use this code: \n");




    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  private ResponseEntity<?> validateAccount(Account account){
    if(account.getUsername().length() > 15 || account.getUsername().length() < 5){
      return sendError(Language.USERNAME_LENGTH_BETWEEN.getString() + " 6 - 15", HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

}
