package com.goode.repository;

import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Long> {

  GroupMember findGroupMemberByGroupAndAccount(Group group, Account account);
  List<GroupMember> findGroupMembersByAccount(Account account);
}
