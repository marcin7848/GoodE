package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.service.AccountService;
import com.goode.service.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GroupMemberValidator extends BaseValidator {

  @Autowired
  GroupMemberService groupMemberService;

  @Autowired
  AccountService accountService;

  public boolean validatePermissionToGroup(Group group, boolean admin, ErrorCode errorCode) {
    Account loggedAccount = accountService.getLoggedAccount();
    if(loggedAccount.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)){
      return true;
    }

    GroupMember groupMember = groupMemberService
        .getGroupMemberByGroupAndAccount(group, loggedAccount);
    if (groupMember == null || (!groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)
        && (!groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_TEACHER) || !admin))) {
      errorCode.rejectValue("accessRole", "error.group.noPermissions");
      return false;
    }

    return true;
  }

  public boolean validateJoinToGroup(Group group, ErrorCode errorCode) {
    if (!group.isPossibleToJoin()) {
      errorCode.rejectValue("possibleToJoin", "error.group.joinToGroup.cannotJoin");
      return false;
    }

    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());
    if(groupMember != null){
      errorCode.rejectValue("possibleToJoin", "error.group.joinToGroup.alreadyJoined");
      return false;
    }

    return true;
  }

  public boolean validateChangeAccessRoleToGroup(Group group, GroupMember groupMember, String newAccessRole, ErrorCode errorCode){
    if(group == null){
      errorCode.rejectValue("group", "error.group.badId");
      return false;
    }

    if(groupMember == null || groupMember.getGroup().getId() != group.getId()){
      errorCode.rejectValue("groupMember", "error.groupMember.memberNotCorrect");
      return false;
    }

    if (!this.validatePermissionToGroup(group, true, errorCode)) {
      errorCode.rejectValue("accessRole", errorCode.getCode());
      return false;
    }

    if(!newAccessRole.equals(AccessRole.ROLE_TEACHER) && !newAccessRole.equals(AccessRole.ROLE_STUDENT)){
      errorCode.rejectValue("accessRole", "error.groupMember.changeAccessRole.badAccessRole");
      return false;
    }

    return true;
  }

  public boolean validateLeaveGroup(Group group, ErrorCode errorCode){
    if(group == null){
      errorCode.rejectValue("group", "error.group.badId");
      return false;
    }

    GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());
    if(groupMember == null){
      errorCode.rejectValue("groupMember", "error.groupMember.leave.badMember");
      return false;
    }

    if(groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)){
      errorCode.rejectValue("accessRole", "error.groupMember.leave.adminCannotLeave");
      return false;
    }

    return true;
  }

  public boolean validateDeleteGroupMember(Group group, GroupMember groupMember, ErrorCode errorCode){
    if(group == null) {
      errorCode.rejectValue("group", "error.group.badId");
      return false;
    }

    if(groupMember == null || group.getId() != groupMember.getGroup().getId()){
      errorCode.rejectValue("groupMember", "error.groupMember.deleteMember.notBelong");
      return false;
    }

    if(groupMember.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)){
      errorCode.rejectValue("accessRole", "error.groupMember.deleteMember.cannotDeleteAdmin");
      return false;
    }

    if (!this.validatePermissionToGroup(group, false, errorCode)) {
      errorCode.rejectValue("accessRole", errorCode.getCode());
      return false;
    }


    return true;
  }

}