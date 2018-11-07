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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

  @Autowired
  AccountService accountService;

  @Override
  public Group getGroupByName(String name) {
    return groupRepository.findGroupByName(name);
  }

  @Override
  public Group getGroupById(int id) {
    return groupRepository.findGroupById(id);
  }

  @Override
  public Group addNew(Group group) {
    group.setCreationTime(new Timestamp(new Date().getTime()));

    Account loggedAccount = accountService.getLoggedAccount();

    int nextOrder = groupRepository.getNextPosition(loggedAccount.getId());
    if (group.getIdGroupParent() != null) {
      nextOrder = groupRepository
          .getNextPositionWithParent(loggedAccount.getId(), group.getIdGroupParent());
    }
    group.setPosition(nextOrder);

    Group newGroup = groupRepository.save(group);
    if (newGroup == null) {
      return null;
    }

    GroupMember groupMember = new GroupMember();
    groupMember.setAccount(loggedAccount);
    groupMember.setGroup(newGroup);
    groupMember.setAccepted(true);
    groupMember.setAccessRole(accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_ADMIN));

    if (groupMemberRepository.save(groupMember) == null) {
      return null;
    }

    return newGroup;
  }

  @Override
  public Group edit(Group group) {
    Group editedGroup = groupRepository.save(group);
    if (editedGroup == null) {
      return null;
    }
    return editedGroup;
  }

  @Override
  public void delete(Group group) {
    Integer idGroupParent = group.getIdGroupParent();
    groupRepository.delete(group);
    List<Group> groups;
    if (idGroupParent != null) {
      groups = groupRepository
          .findAllByIdAccountAndIdGroupParent(accountService.getLoggedAccount().getId(),
              idGroupParent);
      group.setIdGroupParent(idGroupParent);
    } else {
      groups = groupRepository
          .findAllByIdAccountAndIdGroupParentNull(accountService.getLoggedAccount().getId());
    }

    groups.sort(Comparator.comparing(Group::getPosition));
    int i = 0;
    for (Group gr : groups) {
      gr.setPosition(i);
      i++;
    }

    groupRepository.saveAll(groups);
  }

  @Override
  public int changePositionWithChangeIdGroupParent(int idGroup, Integer newIdGroupParent) {
    if (newIdGroupParent != null && idGroup == newIdGroupParent) {
      return -1;
    }

    Group group = groupRepository.findGroupById(idGroup);
    if (group == null) {
      return -1;
    }

    Integer idGroupParent = group.getIdGroupParent();

    if (idGroupParent == newIdGroupParent) {
      return -1;
    }
    group.setIdGroupParent(newIdGroupParent);
    Account loggedAccount = accountService.getLoggedAccount();
    int nextOrder = groupRepository.getNextPosition(loggedAccount.getId());
    if (newIdGroupParent != null) {
      nextOrder = groupRepository
          .getNextPositionWithParent(loggedAccount.getId(), newIdGroupParent);
    }
    group.setPosition(nextOrder);
    groupRepository.save(group);

    List<Group> groups = groupRepository.findAllByIdGroupParentOrderByPosition(idGroupParent);
    int i = 0;
    for (Group gr : groups) {
      gr.setPosition(i);
      i++;
    }

    groupRepository.saveAll(groups);
    return nextOrder;
  }

  @Override
  public boolean changePosition(int idGroup, int newPosition, Integer newIdGroupParent) {
    Group group = groupRepository.findGroupById(idGroup);
    if (group == null) {
      return false;
    }

    if (group.getIdGroupParent() != newIdGroupParent) {
      this.changePositionWithChangeIdGroupParent(idGroup, newIdGroupParent);
    }

    List<Group> groups;
    if (newIdGroupParent != null) {
      groups = groupRepository
          .findAllByIdAccountAndIdGroupParent(accountService.getLoggedAccount().getId(),
              newIdGroupParent);
      group.setIdGroupParent(newIdGroupParent);
    } else {
      groups = groupRepository
          .findAllByIdAccountAndIdGroupParentNull(accountService.getLoggedAccount().getId());
    }

    groups.sort(Comparator.comparing(Group::getPosition));
    groups.remove(group.getPosition());
    group.setPosition(newPosition);
    groups.add(group.getPosition(), group);
    int i = 0;
    for (Group gr : groups) {
      gr.setPosition(i);
      i++;
    }

    groupRepository.saveAll(groups);
    return true;
  }

  @Override
  public boolean joinToGroup(Group group) {
    if(!group.isPossibleToJoin()){
      return false;
    }

    AccessRole accessRole = accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_STUDENT);
    Account loggedAccount = accountService.getLoggedAccount();
    boolean accepted = !group.isAcceptance();

    GroupMember groupMember = new GroupMember();
    groupMember.setAccount(loggedAccount);
    groupMember.setGroup(group);
    groupMember.setAccessRole(accessRole);
    groupMember.setAccepted(accepted);

    GroupMember newGroupMember = groupMemberRepository.save(groupMember);
    if(newGroupMember == null){
      return false;
    }

    return true;
  }

}
