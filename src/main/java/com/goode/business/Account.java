package com.goode.business;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Data
public class Account {

  @Id
  @Column(name = "id")
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

  @Column(name = "role")
  @NotNull
  private int id_role;

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

}
