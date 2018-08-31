package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.repository.ActivationCodeRepository;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivationCodeService implements ActivationCodeServiceI {

  @Autowired
  ActivationCodeRepository activationCodeRepository;

  @Override
  public ActivationCode addNew(Account account, int type){
    ActivationCode activationCode = new ActivationCode();
    activationCode.setAccount(account);
    activationCode.setType(type);
    activationCode.setCode(generateRandomString(50));
    activationCode.setCreationTime(new Timestamp(new Date().getTime()));

    return activationCodeRepository.save(activationCode);
  }

  private String generateRandomString(int length){
    String code;
    String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@$*()";
    do {
      StringBuilder salt = new StringBuilder();
      Random rnd = new Random();
      while (salt.length() < length) {
        int index = (int) (rnd.nextFloat() * SALTCHARS.length());
        salt.append(SALTCHARS.charAt(index));
      }
      code = salt.toString();
    }while(activationCodeRepository.getActivationCodeByCode(code) != null);

    return code;
  }

  @Override
  public boolean validateActivationCode(String activationCode) {
    if(activationCode.length() != 50)
      return false;

    if(!activationCode.matches("^[a-zA-Z0-9!@$*()]+$"))
      return false;

    return true;
  }


}
