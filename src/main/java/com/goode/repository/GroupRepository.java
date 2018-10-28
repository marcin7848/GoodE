package com.goode.repository;

import com.goode.business.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {

  Group findGroupByName(String name);
  Group findGroupById(int id);
 }
