package com.goode.business;

import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Account {

  @Id
  @Column(name = "id_account")
  @NotNull
  private int id;

  @Column(name = "username")
  @NotNull
  private String username;

  @Column(name = "email")
  @NotNull
  private String email;

  @Column(name = "password")
  @NotNull
  private String password;

  @Column(name = "register_no")
  @NotNull
  private int register_no;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_access_role")
  @OnDelete(action = OnDeleteAction.NO_ACTION)
  @NotNull
  private AccessRole accessRole;

  @Column(name = "enabled")
  @NotNull
  private boolean enabled;

  @Column(name = "firstname")
  @NotNull
  private String firstname;

  @Column(name = "lastname")
  @NotNull
  private String lastname;

  @Column(name = "creation_time", updatable = false)
  @NotNull
  private Timestamp creationTime;

  @OneToMany(mappedBy = "account")
  private Set<ActivationCode> activationCodes;

  @OneToMany(mappedBy = "account")
  private Set<GroupMember> groupMembers;

}
