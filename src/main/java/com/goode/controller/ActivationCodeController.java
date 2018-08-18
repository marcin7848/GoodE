package com.goode.controller;

import com.goode.service.ActivationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activationCode")
public class ActivationCodeController {

  @Autowired
  private ActivationCodeService activationCodeService;

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public String account(){
    return "activation code added";
  }
}
