package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
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
    GroupMember groupMember = groupMemberService
        .getGroupMemberByGroupAndAccount(group, accountService.getLoggedAccount());
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

}