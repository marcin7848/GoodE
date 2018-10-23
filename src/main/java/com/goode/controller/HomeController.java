package com.goode.controller;

import com.goode.business.Account;
import com.goode.repository.AccountRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/")
public class HomeController {

  @Autowired
  AccountRepository accountRepository;

  @GetMapping
  public ResponseEntity<?> home(){
    Account loggedAccount = accountRepository
        .findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

    JSONObject obj = new JSONObject();
    if(loggedAccount == null) {
      obj.put("loggedUsername", "");
    }else{
      obj.put("loggedUsername", loggedAccount.getUsername());
    }
    return new ResponseEntity<>(obj.toMap(), HttpStatus.OK);
  }

  @GetMapping("/login")
  public String login(){
    return "login";
  }

  @GetMapping("/login/error")
  public String loginError(){
    return "loginError";
  }

  @GetMapping("/login/success")
  public String loginSuccess(){
    return "loginSuccess";
  }

  @GetMapping("/register")
  public String registerView(){
    return "register";
  }

}
