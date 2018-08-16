package com.goode.controller;

import com.goode.business.Account;
import com.goode.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController<Account, AccountService> {

  @Autowired
  private AccountService accountService;

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> account(){
    Account account = new Account();
    super.initializeService(accountService);
    return super.addNew(account);
  }
}
