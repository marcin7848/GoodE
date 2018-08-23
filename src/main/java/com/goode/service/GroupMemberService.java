package com.goode.service;

import com.goode.repository.GroupMemberRepository;
import com.goode.repository.IGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService implements IGroupMemberService {

  @Autowired
  GroupMemberRepository groupMemberRepository;



}
