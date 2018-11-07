package com.goode.service;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import com.goode.repository.GroupMemberRepository;
import java.util.List;
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

  @Override
  public List<GroupMember> getGroupMembersByAccount(Account account){
    return groupMemberRepository.findGroupMembersByAccount(account);
  }

  @Override
  public boolean acceptNewMember(Group group, Account account){
    GroupMember groupMember = groupMemberRepository.findGroupMemberByGroupAndAccount(group, account);
    if(groupMember == null){
      return false;
    }

    groupMember.setAccepted(true);

    groupMember = groupMemberRepository.save(groupMember);
    if(groupMember == null){
      return false;
    }

    return true;
  }

}
