package com.goode.service;

import com.goode.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService implements IGroupService {

  @Autowired
  GroupRepository groupRepository;



}
