package com.goode.service;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService implements GroupMemberServiceI {

  @Autowired
  GroupMemberRepository groupMemberRepository;

  @Override
  public GroupMember getGroupMemberByGroupAndAccount(Group group, Account account){
    return groupMemberRepository.findGroupMemberByGroupAndAccount(group, account);
  }


}
