package com.goode.service;

import com.goode.business.Group;

public interface GroupServiceI {

  Group getGroupByName(String name);
  Group getGroupById(int id);
  int changePositionWithChangeIdGroupParent(int id, Integer newIdGroupParent);
  boolean changePosition(int idGroup, int newPosition, Integer newIdGroupParent);
}
