package com.goode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

  @GetMapping
  public String home(){
    return "home2223";
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
