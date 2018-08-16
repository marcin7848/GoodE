package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.repository.ActivationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService implements IActivationCodeService{

  @Autowired
  ActivationCodeRepository activationCodeRepository;

  public void addActivationCode(){
    Account account = new Account();
    account.setId(2);
    activationCodeRepository.save(new ActivationCode(2, account, 2, "TEST"));
  }

}
