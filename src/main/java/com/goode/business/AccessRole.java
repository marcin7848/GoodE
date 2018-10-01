package com.goode.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "access_roles")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class AccessRole {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_TEACHER = "ROLE_TEACHER";
  public static final String ROLE_STUDENT = "ROLE_STUDENT";

  @Id
  @Column(name = "id_access_role")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "role")
  @NotNull
  private String role;

  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "accessRole")
  @ToString.Exclude
  private List<Account> accounts;

  @JsonIgnore
  @OneToMany(mappedBy = "accessRole")
  @ToString.Exclude
  private List<GroupMember> groupMembers;
}
