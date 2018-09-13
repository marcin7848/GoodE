package com.goode.validator;

import com.goode.business.Account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AccountValidator implements Validator {

  @Override
  public boolean supports(Class clazz) {
    return Account.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    Account account = (Account) target;

    if(account.getUsername() == null || account.getUsername().isEmpty()) {
      errors.rejectValue("username", "jakis_blad");
    }

  }
}
