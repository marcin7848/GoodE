package com.goode.repository;

import com.goode.business.Group;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface GroupRepository extends CrudRepository<Group, Long> {

  Group findGroupByName(String name);
  Group findGroupById(int id);
  List<Group> findAllByIdGroupParentOrderByPosition(Integer idGroupParent);

  @Query(value = "select COALESCE(MAX(g.position)+1, '0') as nextorder from groups g, group_members gm where gm.id_account=:id_account and g.id_group = gm.id_group and g.id_group_parent=:id_group_parent", nativeQuery = true)
  int getNextPositionWithParent(@Param("id_account") int id_account, @Param("id_group_parent") Integer id_group_parent);

  @Query(value = "select COALESCE(MAX(g.position)+1, '0') as nextorder from groups g, group_members gm where gm.id_account=:id_account and g.id_group = gm.id_group and g.id_group_parent is NULL", nativeQuery = true)
  int getNextPosition(@Param("id_account") int id_account);

  @Query(value = "select * from groups g, group_members gm where gm.id_account=:id_account and g.id_group = gm.id_group and g.id_group_parent=:id_group_parent", nativeQuery = true)
  List<Group> findAllByIdAccountAndIdGroupParent(@Param("id_account") int id_account, @Param("id_group_parent") int id_group_parent);

  @Query(value = "select * from groups g, group_members gm where gm.id_account=:id_account and g.id_group = gm.id_group and g.id_group_parent is NULL", nativeQuery = true)
  List<Group> findAllByIdAccountAndIdGroupParentNull(@Param("id_account") int id_account);

  @Query(value = "select * from groups g, group_members gm where gm.id_account=:id_account and g.id_group = gm.id_group order by position ASC", nativeQuery = true)
  List<Group> findAllByIdAccount(@Param("id_account") int id_account);

  @Query(value = "select * from groups g order by position ASC", nativeQuery = true)
  List<Group> getAllGroups();

  @Query(value = "select * from groups g where hidden is false order by position ASC", nativeQuery = true)
  List<Group> getAllGroupsNotHidden();

}
