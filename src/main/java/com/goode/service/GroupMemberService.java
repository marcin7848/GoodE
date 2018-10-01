package com.goode.service;

import com.goode.repository.GroupMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberService implements GroupMemberServiceI {

  @Autowired
  GroupMemberRepository groupMemberRepository;



}
