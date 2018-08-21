package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;

public interface IActivationCodeService {

  ActivationCode addNew(Account account, int type);
  boolean validateActivationCode(String activationCode);

}
