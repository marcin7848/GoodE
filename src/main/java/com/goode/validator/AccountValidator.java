package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.Language;
import com.goode.business.Account;

import com.goode.business.Account.FullValidation;
import com.goode.repository.AccountRepository;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator extends BaseValidator {

  @Autowired
  private AccountRepository accountRepository;

  public Account validateEmail(String email, ErrorCode errorCode) {
    Account account = new Account();
    account.setEmail(email);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<Account>> errors = validator
        .validateProperty(account, "email", FullValidation.class);

    if (!errors.isEmpty()) {
      errorCode.rejectValue("email", "validate.email.incorrect");
      return null;
    }

    account = accountRepository.findAccountByEmail(email);
    if (account == null) {
      errorCode.rejectValue("email", "validate.email.incorrect");
      return null;
    }

    return account;
  }

  public void validatePassword(String password, ErrorCode errorCode) {
    Account account = new Account();
    account.setPassword(password);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<Account>> errors = validator
        .validateProperty(account, "password", FullValidation.class);

    if (!errors.isEmpty()) {
      ConstraintViolation<Account> accountConstraintViolation = errors.iterator().next();
      errorCode.rejectValue("password", Language
              .translateError(accountConstraintViolation.getPropertyPath().toString(),
                  accountConstraintViolation.getMessageTemplate(),
                  accountConstraintViolation.getMessage(),
                  accountConstraintViolation.getPropertyPath().toString(),
                  Language.getMessage(accountConstraintViolation.getPropertyPath().toString()))
          );
    }
  }

  public Account validateAccountActivation(String email, ErrorCode errorCode) {
    Account account = validateEmail(email, errorCode);
    if (account == null) {
      return null;
    }

    if (account.isEnabled()) {
      errorCode.rejectValue("email", "validate.email.account.activation.alreadyEnabled");
      return null;
    }

    return account;
  }
}
