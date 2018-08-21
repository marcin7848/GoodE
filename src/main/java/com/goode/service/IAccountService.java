package com.goode.service;

import com.goode.business.Account;

public interface IAccountService {

  Account resendActivationCodeValidation(String email);
  Account resendActivationCode(Account account);


}
