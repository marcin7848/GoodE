package com.goode.controller;

import com.goode.business.Group;
import com.goode.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupController extends BaseController<Group, GroupService> {

  @Autowired
  GroupService groupService;


}
