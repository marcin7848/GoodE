package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.repository.AccountRepository;
import com.goode.repository.GroupMemberRepository;
import com.goode.repository.GroupRepository;
import com.goode.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GroupValidator extends BaseValidator {

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  GroupMemberRepository groupMemberRepository;

  @Autowired
  AccountService accountService;

  public boolean validateIdGroupParent(int idGroupParent, ErrorCode errorCode){

    Group group = groupRepository.findGroupById(idGroupParent);

    if(group == null){
      errorCode.rejectValue("idGroupParent", "validate.group.idGroupParent.notExists");
      return false;
    }

    Account loggedAccount = accountService.getLoggedAccount();

    if(loggedAccount == null){
      errorCode.rejectValue("account", "error.authorization");
      return false;
    }

    GroupMember groupMember = groupMemberRepository.getGroupMemberByGroupAndAccount(group, loggedAccount);
    AccessRole accessRole = groupMember.getAccessRole();

    if(!accessRole.getRole().equals(AccessRole.ROLE_ADMIN) && !accessRole.getRole()
        .equals(AccessRole.ROLE_TEACHER)){
      errorCode.rejectValue("accessRole", "validate.group.accessRole.notAccessToParentGroup");
      return false;
    }

    return true;
  }
}
