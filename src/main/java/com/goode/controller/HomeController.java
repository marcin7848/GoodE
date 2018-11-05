package com.goode.controller;

import com.goode.business.Account;
import com.goode.repository.AccountRepository;
import com.goode.service.AccountService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/")
public class HomeController {

  @GetMapping
  public ResponseEntity<?> home(){
    Account loggedAccount = null;
    if(SecurityContextHolder.getContext().getAuthentication() != null) {
      loggedAccount = AccountService.getLoggedAccount();
    }

    JSONObject obj = new JSONObject();
    if(loggedAccount == null) {
      obj.put("loggedUsername", "");
    }else{
      obj.put("loggedUsername", loggedAccount.getUsername());
    }
    return new ResponseEntity<>(obj.toMap(), HttpStatus.OK);
  }

  @PostMapping("/login")
  public boolean login(@RequestParam("username") String username,
      @RequestParam("password") String password){
    return true;
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
