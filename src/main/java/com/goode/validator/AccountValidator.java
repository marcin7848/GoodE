package com.goode.validator;

import com.goode.business.Account;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AccountValidator extends BaseValidator  {

  public void validateAccount(Object target, Errors errors) {
    if(!errors.hasErrors()) {
      Account account = (Account) target;

      if (!account.getUsername().matches("^[a-zA-Z0-9_]+$")) {
        errors.rejectValue("username", "validate.lettersNumbersAndUnderscoreOnly");
      }

      if (!account.getEmail().matches("^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$")) {
        errors.rejectValue("email", "validate.email.incorrect");
      }

      if (!account.getFirstName().matches("^[a-zA-Z-]+$")) {
        errors.rejectValue("firstName", "validate.lettersOnly");
      }

      if (!account.getLastName().matches("^[a-zA-Z-]+$")) {
        errors.rejectValue("lastName", "validate.lettersOnly");
      }
    }
  }
}
