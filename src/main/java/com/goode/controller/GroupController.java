package com.goode.controller;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.Group.AddNewValidation;
import com.goode.business.GroupMember;
import com.goode.repository.AccessRoleRepository;
import com.goode.service.AccountService;
import com.goode.service.GroupMemberService;
import com.goode.service.GroupService;
import com.goode.validator.GroupMemberValidator;
import com.goode.validator.GroupValidator;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @Autowired
  GroupMemberValidator groupMemberValidator;

  @Autowired
  AccessRoleRepository accessRoleRepository;

  @GetMapping("/getMyGroups")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> getMyGroups(){
    return new ResponseEntity<>(groupService.getMyGroups(), HttpStatus.OK);
  }

  @GetMapping("/getAllGroups")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> getAllGroups(){
    return new ResponseEntity<>(groupService.getAllGroupsNotHidden(), HttpStatus.OK);
  }

  @GetMapping("/{id}/view")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> viewGroup(@PathVariable("id") int id){
    Group currentGroup = groupService.getGroupById(id);
    if(currentGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    currentGroup.setPassword("");

    Account loggedAccount = accountService.getLoggedAccount();
    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(currentGroup, loggedAccount);

    if(loggedAccount.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)) {
      groupMember = new GroupMember();
      AccessRole accessRole = accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_ADMIN);
      groupMember.setAccessRole(accessRole);
    }

    if(groupMember != null){
      groupMember.setGroup(null);
    }

    List<GroupMember> groupMembers = new ArrayList<>();
    groupMembers.add(groupMember);
    currentGroup.setGroupMembers(groupMembers);

    return new ResponseEntity<>(currentGroup, HttpStatus.OK);
  }

  @GetMapping("/{id}/getGroupMembers")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> getGroupMembers(@PathVariable("id") int id){
    Group currentGroup = groupService.getGroupById(id);
    if(currentGroup == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateStudentInGroup(currentGroup, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    Account loggedAccount = accountService.getLoggedAccount();

    List<GroupMember> groupMembers = groupMemberService.getGroupMembersByGroup(currentGroup);
    for(int i=0; i<groupMembers.size(); i++){
      groupMembers.get(i).setGroup(null);
      groupMembers.get(i).getAccount().setPassword("");
      groupMembers.get(i).getAccount().setActivationCodes(null);
      if(loggedAccount.getAccessRole().getRole().equals(AccessRole.ROLE_STUDENT)){
        groupMembers.get(i).getAccount().setEmail("");
        groupMembers.get(i).getAccount().setId(0);
        if(groupMembers.get(i).getAccessRole().getRole().equals(AccessRole.ROLE_STUDENT)) {
          groupMembers.get(i).getAccount().setFirstName("");
          groupMembers.get(i).getAccount().setLastName("");
        }
      }
    }

    return new ResponseEntity<>(groupMembers, HttpStatus.OK);
  }

  @PostMapping("/addNew")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"')")
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
          .send(Language.getMessage("error.group.notCreated"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @PatchMapping("/{id}/edit")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"')")
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

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(groupService.getGroupByName(group.getName()) != null && !currentGroup.getName()
        .equals(group.getName())){
      return ErrorMessage
          .send(Language.getMessage("error.group.alreadyExists"), HttpStatus.BAD_REQUEST);
    }

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
          .send(Language.getMessage("error.group.notEdited"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/changePosition/{newPosition}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"')")
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

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    boolean changed = groupService.changePosition(group.getId(), newPosition, newIdGroupParent);
    if(!changed){
      return ErrorMessage
          .send(Language.getMessage("error.group.notChangedPosition"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @DeleteMapping("/{id}/delete")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"')")
  public ResponseEntity<?> delete(@PathVariable("id") int id) {

    Group group = groupService.getGroupById(id);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    groupService.delete(group);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{id}/join")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> joinToGroup(@PathVariable("id") int id) {

    Group group = groupService.getGroupById(id);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validateJoinToGroup(group, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(!groupService.joinToGroup(group)){
      return ErrorMessage
          .send(Language.getMessage("error.group.joinToGroup.internalError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if(group.isAcceptance()){
      return ErrorMessage
          .send(Language.getMessage("group.joinToGroup.needAcceptance"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/member/{idGroupMember}/accept")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> acceptNewMember(@PathVariable("id") int id,
      @PathVariable("idGroupMember") int idGroupMember) {

    Group group = groupService.getGroupById(id);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    GroupMember groupMember = groupMemberService.getGroupMemberById(idGroupMember);
    if(groupMember == null || groupMember.getGroup().getId() != group.getId()){
      return ErrorMessage
          .send(Language.getMessage("error.groupMember.memberNotCorrect"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, false, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(!groupMemberService.acceptNewMember(groupMember)){
      return ErrorMessage
          .send(Language.getMessage("error.group.accept.internalError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/member/{idGroupMember}/changeAccessRole/{newAccessRole}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"')")
  public ResponseEntity<?> changeAccessRoleToGroup(@PathVariable("id") int id,
      @PathVariable("idGroupMember") int idGroupMember,
      @PathVariable("newAccessRole") String newAccessRole) {

    Group group = groupService.getGroupById(id);
    GroupMember groupMember = groupMemberService.getGroupMemberById(idGroupMember);

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateChangeAccessRoleToGroup(group, groupMember, newAccessRole, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(!groupMemberService.changeAccessRoleToGroup(groupMember, newAccessRole)){
      return ErrorMessage
          .send(Language.getMessage("error.groupMember.changeAccessRole.internalError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @DeleteMapping("/{id}/leave")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> leaveGroup(@PathVariable("id") int id) {

    Group group = groupService.getGroupById(id);

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateLeaveGroup(group, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());

    if(!groupMemberService.removeGroupMember(groupMember)){
      return ErrorMessage
          .send(Language.getMessage("error.groupMember.leave.internalError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @DeleteMapping("/{id}/deleteGroupMember/{idGroupMember}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> deleteGroupMember(@PathVariable("id") int id,
      @PathVariable("idGroupMember") int idGroupMember) {

    Group group = groupService.getGroupById(id);
    GroupMember groupMember = groupMemberService.getGroupMemberById(idGroupMember);

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateDeleteGroupMember(group, groupMember, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(!groupMemberService.removeGroupMember(groupMember)){
      return ErrorMessage
          .send(Language.getMessage("error.groupMember.deleteGroupMember.internalError"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

}
