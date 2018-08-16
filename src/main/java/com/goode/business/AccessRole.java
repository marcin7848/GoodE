package com.goode.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "access_roles")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class AccessRole {

  @Id
  @Column(name = "id_access_role")
  @NotNull
  private int id;

  @Column(name = "role")
  @NotNull
  private String role;

  @JsonIgnore
  @OneToMany(mappedBy = "accessRole")
  private Set<Account> accounts;

  @JsonIgnore
  @OneToMany(mappedBy = "accessRole")
  private Set<GroupMember> groupMembers;
}
