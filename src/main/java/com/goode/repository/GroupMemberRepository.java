package com.goode.repository;

import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Group;
import com.goode.business.GroupMember;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Long> {

  GroupMember findGroupMemberByGroupAndAccount(Group group, Account account);
  List<GroupMember> findGroupMembersByAccount(Account account);
  GroupMember findGroupMemberById(int id);

  @Query(value = "select * from group_members where id_account=:id_account and (id_access_role=:id_access_role_admin or id_access_role=:id_access_role_teacher)", nativeQuery = true)
  List<GroupMember> findGroupMemberByAccountWithAdminAndTeacherRights(@Param("id_account") int id_account, @Param("id_access_role_admin") int id_access_role_admin, @Param("id_access_role_teacher") int id_access_role_teacher);
}
