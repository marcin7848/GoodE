package com.goode.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "group_members")
@Entity
@NoArgsConstructor
@Data
public class GroupMember {

  @Id
  @Column(name = "id")
  @NotNull
  private int id;

  @Column(name = "id_account")
  @NotNull
  private int id_account;

  @Column(name = "id_group")
  @NotNull
  private int id_group;

  @Column(name = "id_role")
  @NotNull
  private int id_role;

}
