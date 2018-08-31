package com.goode.controller;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.service.GroupService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
public class GroupController extends BaseController<Group, GroupService> {

  @Autowired
  GroupService groupService;

  @GetMapping
  public ResponseEntity<?> addNew(Principal principal){

    Account account = new Account();
    account.setUsername(principal.getName());

    return new ResponseEntity<>(account, HttpStatus.OK);
  }


}
