package com.goode.service;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.repository.AccessRoleRepository;
import com.goode.repository.AccountRepository;
import com.goode.repository.GroupMemberRepository;
import com.goode.repository.GroupRepository;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GroupService implements GroupServiceI, StandardizeService<Group> {

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  GroupMemberRepository groupMemberRepository;

  @Autowired
  AccessRoleRepository accessRoleRepository;

  @Override
  public Group getGroupByName(String name){
    return groupRepository.findGroupByName(name);
  }

  @Override
  public Group addNew(Group group){
    group.setCreationTime(new Timestamp(new Date().getTime()));

    Account loggedAccount = AccountService.getLoggedAccount();

    int nextOrder = groupRepository.getNextPosition(loggedAccount.getId());
    if(group.getIdGroupParent() != null) {
      nextOrder = groupRepository
          .getNextPositionWithParent(loggedAccount.getId(), group.getIdGroupParent());
    }
    group.setPosition(nextOrder);

    Group newGroup = groupRepository.save(group);
    if(newGroup == null){
      return null;
    }

    GroupMember groupMember = new GroupMember();
    groupMember.setAccount(loggedAccount);
    groupMember.setGroup(newGroup);
    groupMember.setAccepted(true);
    groupMember.setAccessRole(accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_ADMIN));

    if(groupMemberRepository.save(groupMember) == null){
      return null;
    }

    return newGroup;
  }
}
