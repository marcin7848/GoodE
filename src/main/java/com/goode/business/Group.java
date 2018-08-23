package com.goode.business;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "groups")
@Entity
@NoArgsConstructor
@Data
public class Group {

  @Id
  @Column(name = "id_group")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "name")
  @NotNull
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "password")
  private String password;

  @Column(name = "can_join")
  @NotNull
  private boolean can_join;

  @Column(name = "need_acceptance")
  @NotNull
  private boolean need_acceptance;

  @Column(name = "is_show")
  @NotNull
  private boolean is_show;

  @Column(name = "id_group_parent")
  private int id_group_parent;

  @Column(name = "order")
  @NotNull
  private int order;

  @Column(name = "creation_time", updatable = false)
  @NotNull
  private Timestamp creationTime;

  @OneToMany(mappedBy = "group")
  private List<GroupMember> groupMembers;
}
