package com.goode.business;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "role", nullable = false)
  private String role;

  @Column(name = "enabled", nullable = false)
  private boolean enabled;

  @Column(name = "firstname", nullable = false)
  private String firstname;

  @Column(name = "lastname", nullable = false)
  private String lastname;

  @Column(name = "creation_time", nullable = false)
  private Timestamp creationTime;


  Account(){}

}
