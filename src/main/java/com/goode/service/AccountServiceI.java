package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import java.security.Principal;

public interface AccountServiceI {

  Account generateActivationCode(Account account, int type);
  boolean activateAccount(ActivationCode activationCode);
  ActivationCode resetPasswordRequest(String activationCode);
  boolean resetPassword(ActivationCode activationCode, String newPassword);
  boolean changeAccessRole(Account account, String role);
  Account getAccountByPrincipal(Principal principal);
  Account getAccountById(int id_account);

}
