package com.goode.service;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;

public interface GroupMemberServiceI {

  GroupMember getGroupMemberByGroupAndAccount(Group group, Account account);
}
