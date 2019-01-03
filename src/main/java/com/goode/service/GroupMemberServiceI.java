package com.goode.service;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import java.util.List;

public interface GroupMemberServiceI {

  GroupMember getGroupMemberByGroupAndAccount(Group group, Account account);
  List<GroupMember> getGroupMembersByAccount(Account account);
  GroupMember getGroupMemberById(int idGroupMember);
  List<GroupMember> getGroupMembersByGroup(Group group);
  boolean acceptNewMember(GroupMember groupMember);
  boolean changeAccessRoleToGroup(GroupMember groupMember, String newAccessRole);
  void removeAccountTeacherIfNotTeacherForAnyGroup(Account account);
  boolean removeGroupMember(GroupMember groupMember);
}
