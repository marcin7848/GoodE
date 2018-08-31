package com.goode.service;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.repository.AccessRoleRepository;
import com.goode.repository.AccountRepository;
import com.goode.repository.ActivationCodeRepository;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements AccountServiceI, StandardizeService<Account>{

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccessRoleRepository accessRoleRepository;

  @Autowired
  private ActivationCodeRepository activationCodeRepository;

  @Autowired
  private ActivationCodeService activationCodeService;

  public Account getAccountByPrincipal(Principal principal){
    return accountRepository.findAccountByUsername(principal.getName()) ;
  }

  public Account getAccountById(int id_account){
    return accountRepository.findAccountById(id_account);
  }

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

    ActivationCode activationCode = activationCodeService.addNew(newAccount, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);
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

    if(!validateEmail(account.getEmail()))
      return false;

    if(!validatePassword(account.getPassword()))
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

  private boolean validateEmail(String email){
    if(email == null)
      return false;

    return email.length() <= 100 && email.length() >= 5 &&
        email.matches("^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$");
  }

  private boolean validatePassword(String password){
    if(password == null)
      return false;

    return password.length() <= 40 && password.length() >= 8;
  }

  @Override
  public Account resendActivationCodeValidation(String email) {
    if (!validateEmail(email))
      return null;

    Account account = accountRepository.findAccountByEmail(email);
    if (account == null)
      return null;

    if (account.isEnabled())
      return null;

    return account;
  }

  @Override
  public Account generateActivationCode(Account account, int type) {
    List<ActivationCode> listToDelete = activationCodeRepository.getAllAddedAtLeastXHoursAgo(account.getId(), type, 2);
    activationCodeRepository.deleteAll(listToDelete);

    List<ActivationCode> list = activationCodeRepository.getActivationCodesByAccountAndType(account, type);
    if(list.size() > 3)
      return null;

    ActivationCode activationCode = activationCodeService.addNew(account, type);

    if(activationCode == null)
      return null;

    List<ActivationCode> listActivationCodes = new ArrayList<>();
    listActivationCodes.add(activationCode);
    account.setActivationCodes(listActivationCodes);

    return account;
  }

  @Override
  public boolean activateAccount(String activationCode){
    if(!activationCodeService.validateActivationCode(activationCode))
      return false;

    ActivationCode activationCode1 = activationCodeRepository.getActivationCodeByCodeAndType(activationCode, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);

    if(activationCode1 == null)
      return false;

    Account account = activationCode1.getAccount();
    account.setEnabled(true);

    Account updatedAccount = accountRepository.save(account);

    if(updatedAccount == null)
      return false;

    List<ActivationCode> activationCodeList = activationCodeRepository.getActivationCodesByAccountAndType(updatedAccount, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);

    activationCodeRepository.deleteAll(activationCodeList);

    return true;
  }

  @Override
  public Account sendResetPasswordValidation(String email){
    if (!validateEmail(email))
      return null;

    Account account = accountRepository.findAccountByEmail(email);
    if (account == null)
      return null;

    return account;
  }


  @Override
  public ActivationCode resetPasswordRequest(String activationCode){
    if(!activationCodeService.validateActivationCode(activationCode))
      return null;

    ActivationCode activationCode1 = activationCodeRepository.getActivationCodeByCodeAndType(activationCode, ActivationCode.TYPE_RESET_PASSWORD_CODE);

    if(activationCode1 == null)
      return null;

    return activationCode1;
  }

  @Override
  public boolean resetPassword(String activationCode, String newPassword){
    ActivationCode activationCode1 = this.resetPasswordRequest(activationCode);
    if(activationCode1 == null)
      return false;

    if(!validatePassword(newPassword))
      return false;

    Account account = activationCode1.getAccount();
    account.setPassword(hashPassword(newPassword));

    Account updatedAccount = accountRepository.save(account);
    if(updatedAccount == null)
      return false;

    List<ActivationCode> activationCodeList = activationCodeRepository.getActivationCodesByAccountAndType(updatedAccount, ActivationCode.TYPE_RESET_PASSWORD_CODE);

    activationCodeRepository.deleteAll(activationCodeList);

    return true;
  }

  @Override
  public boolean changeAccessRole(int accountId, String role){
    if(accountId == Account.MAIN_ADMINISTRATOR_ID)
      return false;

    Account loggedAccount = accountRepository.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    if(loggedAccount == null || (loggedAccount.getId() != Account.MAIN_ADMINISTRATOR_ID && role.equals(AccessRole.ROLE_ADMIN)))
      return false; //cannot set ROLE_ADMIN if you are not main admin

    Account account = accountRepository.findAccountById(accountId);
    if(account == null || (loggedAccount.getId() != Account.MAIN_ADMINISTRATOR_ID && account.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)))
      return false; //cannot change to other role for ADMIN if you are not a main admin

    AccessRole accessRole = accessRoleRepository.getAccessRoleByRole(role);
    if(accessRole == null)
      return false;

    account.setAccessRole(accessRole);

    account = accountRepository.save(account);
    return account != null;
  }

}
