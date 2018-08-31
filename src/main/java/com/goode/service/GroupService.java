package com.goode.service;

import com.goode.business.Group;
import com.goode.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService implements GroupServiceI, StandardizeService<Group> {

  @Autowired
  GroupRepository groupRepository;

  @Override
  public Group addNew(Group group){
    return null;
  }
}
