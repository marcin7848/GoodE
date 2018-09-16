package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.Account;

import com.goode.repository.AccountRepository;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator extends BaseValidator  {

  @Autowired
  private AccountRepository accountRepository;

  public Account validateAccountActivation(String email, ErrorCode errorCode){
    Account account = new Account();
    account.setEmail(email);
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<Account>> errors = validator.validateProperty(account, "email", Account.ValidationStepTwo.class);

    if(!errors.isEmpty()) {
      errorCode.rejectValue("email", "validate.email.incorrect");
      return null;
    }

    account = accountRepository.findAccountByEmail(email);
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
