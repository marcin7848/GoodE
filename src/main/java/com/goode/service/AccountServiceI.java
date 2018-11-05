package com.goode.service;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.ActivationCode;
import java.security.Principal;
import java.util.List;

public interface AccountServiceI {

  Account getAccountByPrincipal(Principal principal);
  Account getAccountById(int id_account);
  Account getAccountByUsername(String username);
  Account getAccountByEmail(String email);
  Account getLoggedAccount();
  Account generateActivationCode(Account account, int type);
  boolean activateAccount(ActivationCode activationCode);
  ActivationCode resetPasswordRequest(String activationCode);
  boolean resetPassword(ActivationCode activationCode, String newPassword);
  boolean changeAccessRole(Account account, String role);
  Iterable<AccessRole> getAllAccessRole();
  Iterable<Account> getAllAccounts();
  List<Account> getAccountByUsernameOrEmail(String username, String email);
}
