package com.goode.controller;

import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.Group.AddNewValidation;
import com.goode.service.GroupService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
  public ResponseEntity<?> addNew(@Validated(AddNewValidation.class) @RequestBody Group group,
      BindingResult result){
    super.initializeService(groupService);

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }


    Group newGroup = groupService.addNew(group);
    if(newGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.notCreated"), HttpStatus.BAD_REQUEST);
    }


    return new ResponseEntity<>(null, HttpStatus.OK);
  }


}
