package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;

public interface AccountServiceI {

  Account resendActivationCodeValidation(String email);
  Account generateActivationCode(Account account, int type);
  boolean activateAccount(String activationCode);
  Account sendResetPasswordValidation(String resetPassword);
  ActivationCode resetPasswordRequest(String activationCode);
  boolean resetPassword(String activationCode, String newPassword);


}
