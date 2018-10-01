package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccessRoleValidator extends BaseValidator {

  @Autowired
  AccountRepository accountRepository;

  public Account validateChangeAccountAccessRole(int accountId, String role, ErrorCode errorCode){
    if (accountId == Account.MAIN_ADMINISTRATOR_ID) {
      errorCode.rejectValue("accountId", "error.account.changeAccessRole.mainAdmin");
      return null;
    }

    Account loggedAccount = accountRepository
        .findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    if (loggedAccount == null || (loggedAccount.getId() != Account.MAIN_ADMINISTRATOR_ID && role
        .equals(AccessRole.ROLE_ADMIN))) {
      errorCode.rejectValue("role", "error.account.changeAccessRole.setRoleAdmin");
      return null;
    }

    Account account = accountRepository.findAccountById(accountId);
    if (account == null || (loggedAccount.getId() != Account.MAIN_ADMINISTRATOR_ID && account
        .getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN))) {
      errorCode.rejectValue("role", "error.account.changeAccessRole.changeRoleAdmin");
      return null;
    }

    return account;
  }
}
