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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements AccountServiceI, StandardizeService<Account> {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private AccessRoleRepository accessRoleRepository;

  @Autowired
  private ActivationCodeRepository activationCodeRepository;

  @Autowired
  private ActivationCodeService activationCodeService;

  @Override
  public Account getLoggedAccount() {
    return this.getAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @Override
  public Account getAccountByPrincipal(Principal principal) {
    if(principal != null)
      return accountRepository.findAccountByUsername(principal.getName());
    return null;
  }

  @Override
  public Account getAccountById(int id_account) {
    return accountRepository.findAccountById(id_account);
  }

  @Override
  public Iterable<AccessRole> getAllAccessRole(){
    return accessRoleRepository.findAll();
  }

  @Override
  public Account getAccountByUsername(String username) {
    return accountRepository.findAccountByUsername(username);
  }

  @Override
  public List<Account> getAccountByUsernameOrEmail(String username, String email) {
    return accountRepository.findAccountByUsernameOrEmail(username, email);
  }

  @Override
  public Account getAccountByEmail(String email) {
    return accountRepository.findAccountByEmail(email);
  }

  @Override
  public Iterable<Account> getAllAccounts(){
    return accountRepository.findAll();
  }

  @Override
  public Account addNew(Account account) {
    account.setEnabled(false);
    account.setAccessRole(accessRoleRepository.getAccessRoleById(3));
    account.setCreationTime(new Timestamp(new Date().getTime()));
    account.setPassword(this.hashPassword(account.getPassword()));

    Account newAccount = accountRepository.save(account);
    if (newAccount == null) {
      return null;
    }

    ActivationCode activationCode = activationCodeService
        .addNew(newAccount, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);
    if (activationCode == null) {
      return null;
    }

    List<ActivationCode> listActivationCodes = new ArrayList<>();
    listActivationCodes.add(activationCode);

    newAccount.setActivationCodes(listActivationCodes);

    return newAccount;
  }

  @Override
  public Account edit(Account account) {
    account.setPassword(this.hashPassword(account.getPassword()));
    Account editedAccount = accountRepository.save(account);
    if (editedAccount == null) {
      return null;
    }
    return editedAccount;
  }

  @Override
  public void delete(Account account){
  }

  private String hashPassword(String password) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    return passwordEncoder.encode(password);
  }

  @Override
  public Account generateActivationCode(Account account, int type) {
    List<ActivationCode> listToDelete = activationCodeRepository
        .getAllAddedAtLeastXHoursAgo(account.getId(), type, 2);
    activationCodeRepository.deleteAll(listToDelete);

    List<ActivationCode> list = activationCodeRepository
        .getActivationCodesByAccountAndType(account, type);
    if (list.size() > 3) {
      return null;
    }

    ActivationCode activationCode = activationCodeService.addNew(account, type);

    if (activationCode == null) {
      return null;
    }

    List<ActivationCode> listActivationCodes = new ArrayList<>();
    listActivationCodes.add(activationCode);
    account.setActivationCodes(listActivationCodes);

    return account;
  }

  @Override
  public boolean activateAccount(ActivationCode activationCode) {
    Account account = activationCode.getAccount();
    account.setEnabled(true);

    Account updatedAccount = accountRepository.save(account);

    if (updatedAccount == null) {
      return false;
    }

    List<ActivationCode> activationCodeList = activationCodeRepository
        .getActivationCodesByAccountAndType(updatedAccount,
            ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);

    activationCodeRepository.deleteAll(activationCodeList);

    return true;
  }

  @Override
  public ActivationCode resetPasswordRequest(String activationCode) {
    if (!activationCodeService.validateActivationCode(activationCode)) {
      return null;
    }

    ActivationCode activationCode1 = activationCodeRepository
        .getActivationCodeByCodeAndType(activationCode, ActivationCode.TYPE_RESET_PASSWORD_CODE);

    if (activationCode1 == null) {
      return null;
    }

    return activationCode1;
  }

  @Override
  public boolean resetPassword(ActivationCode activationCode, String newPassword) {
    Account account = activationCode.getAccount();
    account.setPassword(hashPassword(newPassword));

    Account updatedAccount = accountRepository.save(account);
    if (updatedAccount == null) {
      return false;
    }

    List<ActivationCode> activationCodeList = activationCodeRepository
        .getActivationCodesByAccountAndType(updatedAccount,
            ActivationCode.TYPE_RESET_PASSWORD_CODE);

    activationCodeRepository.deleteAll(activationCodeList);

    return true;
  }

  @Override
  public boolean changeAccessRole(Account account, String role) {
    AccessRole accessRole = accessRoleRepository.getAccessRoleByRole(role);
    if (accessRole == null) {
      return false;
    }

    account.setAccessRole(accessRole);

    account = accountRepository.save(account);
    return account != null;
  }

}
