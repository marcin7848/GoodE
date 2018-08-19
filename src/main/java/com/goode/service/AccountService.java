package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.repository.AccessRoleRepository;
import com.goode.repository.AccountRepository;
import com.goode.repository.ActivationCodeRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements IAccountService, StandardizeService<Account>{

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccessRoleRepository accessRoleRepository;

  @Autowired
  private ActivationCodeRepository activationCodeRepository;

  @Autowired
  private ActivationCodeService activationCodeService;

  @Override
  public Account addNew(Account account){

    if(!validateAccount(account))
      return null;

    if(!accountRepository.findByUsernameOrEmail(account.getUsername(), account.getEmail()).isEmpty())
      return null;

    account.setEnabled(false);
    account.setAccessRole(accessRoleRepository.getAccessRoleById(3));
    account.setCreationTime(new Timestamp(new Date().getTime()));
    account.setPassword(this.hashPassword(account.getPassword()));

    Account newAccount = accountRepository.save(account);
    if(newAccount == null)
      return null;

    ActivationCode activationCode = activationCodeService.addNew(newAccount, 1);
    if(activationCode == null)
      return null;

    List<ActivationCode> listActivationCodes = new ArrayList<>();
    listActivationCodes.add(activationCode);

    newAccount.setActivationCodes(listActivationCodes);

    return newAccount;
  }

  private boolean validateAccount(Account account){
    if(account.getUsername() == null || account.getUsername().isEmpty() ||
        account.getEmail() == null || account.getEmail().isEmpty() ||
        account.getPassword() == null || account.getPassword().isEmpty() ||
        account.getRegister_no() == null ||
        account.getFirstname() == null || account.getFirstname().isEmpty() ||
        account.getLastname() == null || account.getLastname().isEmpty())
      return false;

    if(account.getUsername().length() > 15 || account.getUsername().length() < 5)
      return false;

    if(!account.getUsername().matches("^[a-zA-Z0-9_]+$"))
      return false;

    if(account.getEmail().length() > 100 || account.getEmail().length() < 5 ||
        !account.getEmail().matches("^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$"))
      return false;

    if(account.getPassword().length() > 100 || account.getPassword().length() < 8)
      return false;

    if(account.getRegister_no() < 1)
      return false;

    if(account.getFirstname().length() < 2 || account.getFirstname().length() > 30)
      return false;

    if(account.getLastname().length() < 2 || account.getLastname().length() > 30)
      return false;

    if(!account.getFirstname().matches("^[a-zA-Z-]+$"))
      return false;

    if(!account.getLastname().matches("^[a-zA-Z-]+$"))
      return false;

    return true;
  }

  private String hashPassword(String password){
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.encode(password);
  }
}
