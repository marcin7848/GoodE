package com.goode.controller;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.service.GroupService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
public class GroupController extends BaseController<Group, GroupService> {

  @Autowired
  GroupService groupService;

  @PostMapping("/addNew")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> addNew(Principal principal, @RequestBody Group group){

    Account account = new Account();
    account.setUsername(principal.getName());

    return new ResponseEntity<>(account, HttpStatus.OK);
  }


}
