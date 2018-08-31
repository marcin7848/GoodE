package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import java.security.Principal;

public interface AccountServiceI {

  Account resendActivationCodeValidation(String email);
  Account generateActivationCode(Account account, int type);
  boolean activateAccount(String activationCode);
  Account sendResetPasswordValidation(String resetPassword);
  ActivationCode resetPasswordRequest(String activationCode);
  boolean resetPassword(String activationCode, String newPassword);
  boolean changeAccessRole(int accountId, String role);
  Account getAccountByPrincipal(Principal principal);
  Account getAccountById(int id_account);

}
