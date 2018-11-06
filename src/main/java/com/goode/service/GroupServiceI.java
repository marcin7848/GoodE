package com.goode.service;

import com.goode.business.Group;

public interface GroupServiceI {

  Group getGroupByName(String name);
  Group getGroupById(int id);
}
