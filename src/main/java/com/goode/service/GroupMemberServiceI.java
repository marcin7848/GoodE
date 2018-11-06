package com.goode.service;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import java.util.List;

public interface GroupMemberServiceI {

  GroupMember getGroupMemberByGroupAndAccount(Group group, Account account);
  List<GroupMember> getGroupMembersByAccount(Account account);
}
