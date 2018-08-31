package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;

public interface ActivationCodeServiceI {

  ActivationCode addNew(Account account, int type);
  boolean validateActivationCode(String activationCode);

}
