package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.Account;

import com.goode.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class AccountValidator extends BaseValidator  {

  @Autowired
  private AccountRepository accountRepository;

  public boolean validateEmail(String email){
    return email.matches("^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$");
  }

  public void validateAccount(Object target, Errors errors) {
    if(!errors.hasErrors()) {
      Account account = (Account) target;

      if (!account.getUsername().matches("^[a-zA-Z0-9_]+$")) {
        errors.rejectValue("username", "validate.lettersNumbersAndUnderscoreOnly");
      }

      if (!validateEmail(account.getEmail())) {
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

  public Account validateAccountActivation(String email, ErrorCode errorCode){
    if (!validateEmail(email)) {
      errorCode.rejectValue("email", "validate.email.incorrect");
      return null;
    }

    Account account = accountRepository.findAccountByEmail(email);
    if (account == null) {
      errorCode.rejectValue("email", "validate.email.incorrect");
      return null;
    }

    if (account.isEnabled()){
      errorCode.rejectValue("email", "validate.email.account.activation.alreadyEnabled");
      return null;
    }

    return account;
  }
}
