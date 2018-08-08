package com.goode.controller;

import com.goode.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

  @Autowired
  private AccountService accountService;

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public String account(){
    accountService.savePerson();
    return "account2";
  }
}
