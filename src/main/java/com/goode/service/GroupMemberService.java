package com.goode.service;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.repository.AccessRoleRepository;
import com.goode.repository.AccountRepository;
import com.goode.repository.GroupMemberRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService implements GroupMemberServiceI {

  @Autowired
  GroupMemberRepository groupMemberRepository;

  @Autowired
  AccountService accountService;

  @Autowired
  AccessRoleRepository accessRoleRepository;

  @Autowired
  AccountRepository accountRepository;

  @Override
  public GroupMember getGroupMemberByGroupAndAccount(Group group, Account account){
    return groupMemberRepository.findGroupMemberByGroupAndAccount(group, account);
  }

  @Override
  public GroupMember getGroupMemberById(int idGroupMember){
    return groupMemberRepository.findGroupMemberById(idGroupMember);
  }

  @Override
  public List<GroupMember> getGroupMembersByAccount(Account account){
    return groupMemberRepository.findGroupMembersByAccount(account);
  }

  @Override
  public List<GroupMember> getGroupMembersByGroup(Group group){
    return groupMemberRepository.findGroupMembersByGroup(group);
  }

  @Override
  public boolean acceptNewMember(GroupMember groupMember){
    groupMember.setAccepted(true);

    groupMember = groupMemberRepository.save(groupMember);
    if(groupMember == null){
      return false;
    }

    return true;
  }

  @Override
  public boolean changeAccessRoleToGroup(GroupMember groupMember, String newAccessRole){
    AccessRole accessRole = accessRoleRepository.getAccessRoleByRole(newAccessRole);
    groupMember.setAccessRole(accessRole);
    groupMember = groupMemberRepository.save(groupMember);
    if(groupMember == null){
      return false;
    }

    Account account = accountService.getAccountById(groupMember.getAccount().getId());
    if(accessRole.getRole().equals(AccessRole.ROLE_TEACHER) && account.getAccessRole().getRole()
        .equals(AccessRole.ROLE_STUDENT)){
      accountService.changeAccessRole(account, AccessRole.ROLE_TEACHER);
    }

    return true;
  }

  @Override
  public void removeAccountTeacherIfNotTeacherForAnyGroup(Account account){
    AccessRole accessRoleAdmin = accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_ADMIN);
    AccessRole accessRoleTeacher = accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_TEACHER);
    List<GroupMember> groupMembers = groupMemberRepository.findGroupMemberByAccountWithAdminAndTeacherRights(account.getId(), accessRoleAdmin.getId(), accessRoleTeacher.getId());

    if(groupMembers.isEmpty() && !account.getAccessRole().getRole().equals(AccessRole.ROLE_ADMIN)){
      AccessRole accessRoleStudent = accessRoleRepository.getAccessRoleByRole(AccessRole.ROLE_STUDENT);
      account.setAccessRole(accessRoleStudent);
      accountRepository.save(account);
    }
  }

  @Override
  public boolean removeGroupMember(GroupMember groupMember){
    Account account = groupMember.getAccount();
    groupMemberRepository.delete(groupMember);
    this.removeAccountTeacherIfNotTeacherForAnyGroup(account);
    return true;
  }


}
