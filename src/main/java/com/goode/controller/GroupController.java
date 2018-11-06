package com.goode.controller;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.Group.AddNewValidation;
import com.goode.business.GroupMember;
import com.goode.repository.GroupMemberRepository;
import com.goode.service.AccountService;
import com.goode.service.GroupMemberService;
import com.goode.service.GroupService;
import com.goode.validator.GroupValidator;
import java.security.Principal;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group")
public class GroupController extends BaseController<Group, GroupService> {

  @Autowired
  GroupService groupService;

  @Autowired
  GroupValidator groupValidator;

  @Autowired
  GroupMemberService groupMemberService;

  @Autowired
  AccountService accountService;

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

    if(groupService.getGroupByName(group.getName()) != null){
      return ErrorMessage
          .send(Language.getMessage("error.group.alreadyExists"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (group.getIdGroupParent() != null && !groupValidator.validateIdGroupParent(group.getIdGroupParent(), errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    Group newGroup = groupService.addNew(group);
    if(newGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.notCreated"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @PatchMapping("/{id}/edit")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> edit(@PathVariable("id") int id,
      @Validated(AddNewValidation.class) @RequestBody Group group,
      BindingResult result) {
    super.initializeService(groupService);

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Group currentGroup = groupService.getGroupById(id);
    if(currentGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    group.setId(currentGroup.getId());
    group.setCreationTime(currentGroup.getCreationTime());
    group.setPosition(currentGroup.getPosition());
    if(group.getIdGroupParent() != null && id == group.getIdGroupParent()){
      group.setIdGroupParent(currentGroup.getIdGroupParent());
    }

    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());
    if(groupMember == null || (!groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)
        && !groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_TEACHER))){
      return ErrorMessage
          .send(Language.getMessage("error.group.edit.noPermissions"), HttpStatus.BAD_REQUEST);
    }

    if(groupService.getGroupByName(group.getName()) != null && !currentGroup.getName()
        .equals(group.getName())){
      return ErrorMessage
          .send(Language.getMessage("error.group.alreadyExists"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (group.getIdGroupParent() != null && !groupValidator.validateIdGroupParent(group.getIdGroupParent(), errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(group.getIdGroupParent() != currentGroup.getIdGroupParent()){
      int newPosition = groupService.changePositionWithChangeIdGroupParent(group.getId(), group.getIdGroupParent());
      if(newPosition != -1){
        group.setPosition(newPosition);
      }
    }

    Group editedGroup = groupService.edit(group);
    if(editedGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.notEdited"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/changePosition/{newPosition}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> changePosition(@PathVariable("id") int id,
      @PathVariable("newPosition") int newPosition,
      @RequestBody Map<String, Object> newIdGroupParentObj) {

    //TODO: dodać implementację w angularze, pamiętać o przekazywaniu newIdGroupParent
    Integer newIdGroupParent = (Integer) newIdGroupParentObj.get("newIdGroupParent");

    Group group = groupService.getGroupById(id);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    if(newPosition < 0){
      return ErrorMessage
          .send(Language.getMessage("error.group.changePosition.badPosition"), HttpStatus.BAD_REQUEST);
    }

    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());
    if(groupMember == null || (!groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)
        && !groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_TEACHER))){
      return ErrorMessage
          .send(Language.getMessage("error.group.changePosition.noPermissions"), HttpStatus.BAD_REQUEST);
    }

    boolean changed = groupService.changePosition(group.getId(), newPosition, newIdGroupParent);
    if(!changed){
      return ErrorMessage
          .send(Language.getMessage("error.group.notChangedPosition"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

}
