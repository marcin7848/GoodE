package com.goode.validator;

import com.goode.business.Account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AccountValidator extends BaseValidator  {

  public void validateId(Object target, Errors errors) {
    Account account = (Account) target;

    if(account.getId() == 1) {
      errors.rejectValue("id", "bad ID");
    }

  }
}
