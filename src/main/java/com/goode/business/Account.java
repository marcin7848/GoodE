package com.goode.business;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Account {

  @Id
  @Column(name = "id_account")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "username")
  @NotNull
  @Length(min = 6, max = 15)
  private String username;

  @Column(name = "email")
  @NotNull
  @Length(min = 6, max = 100)
  private String email;

  @Column(name = "password")
  @NotNull
  @Length(min = 8, max = 100)
  private String password;

  @Column(name = "register_no")
  @NotNull
  private Integer register_no;

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
  @Length(min = 2, max = 30)
  private String firstname;

  @Column(name = "lastname")
  @NotNull
  @Length(min = 2, max = 30)
  private String lastname;

  @Column(name = "creation_time", updatable = false)
  @NotNull
  private Timestamp creationTime;

  @OneToMany(mappedBy = "account")
  private List<ActivationCode> activationCodes;

  @OneToMany(mappedBy = "account")
  private List<GroupMember> groupMembers;

}
