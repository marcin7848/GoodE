package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.repository.ActivationCodeRepository;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService implements IActivationCodeService{

  @Autowired
  ActivationCodeRepository activationCodeRepository;

  public ActivationCode addNew(Account account, int type){
    ActivationCode activationCode = new ActivationCode();
    activationCode.setAccount(account);
    activationCode.setType(type);
    activationCode.setCode(generateRandomString(40));

    return activationCodeRepository.save(activationCode);
  }

  private String generateRandomString(int length){
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@$^*()><";
    StringBuilder salt = new StringBuilder();
    Random rnd = new Random();
    while (salt.length() < length) {
      int index = (int) (rnd.nextFloat() * SALTCHARS.length());
      salt.append(SALTCHARS.charAt(index));
    }
    return salt.toString();
  }
}
