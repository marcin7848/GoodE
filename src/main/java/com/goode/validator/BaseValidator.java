package com.goode.validator;

import com.goode.business.Account;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class BaseValidator implements Validator {

  @Override
  public boolean supports(Class clazz) {
    return Account.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {}

}
