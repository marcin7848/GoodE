package com.goode.service;

import com.goode.business.Account;

public interface IAccountService {

  Account resendActivationCode(String email);
}
