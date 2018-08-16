package com.goode.controller;

import com.goode.business.Account;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

  @GetMapping(produces = "application/json")
  public ResponseEntity<?> home(){
    Account account = new Account();
    account.setId(1);
    account.setEmail("emailxD");
    JSONObject entity = new JSONObject();
    List<Account> list= new ArrayList<>();
    list.add(account);
    entity.put("aa", new JSONArray(list));
    return new ResponseEntity<>(account, HttpStatus.NOT_FOUND);
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

}
