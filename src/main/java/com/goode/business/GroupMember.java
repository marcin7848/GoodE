package com.goode.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Table(name = "group_members")
@Entity
@NoArgsConstructor
@Data
public class GroupMember {

  @Id
  @Column(name = "id_group_member")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_account")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Account account;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_group")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Group group;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_access_role")
  @OnDelete(action = OnDeleteAction.NO_ACTION)
  @NotNull
  private AccessRole accessRole;

  @Column(name = "accepted")
  @NotNull
  private boolean accepted;
}
