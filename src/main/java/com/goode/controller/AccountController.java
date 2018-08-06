package com.goode.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public String account(){
    return "account2";
  }
}
