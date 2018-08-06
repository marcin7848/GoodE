package com.goode.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "access_roles")
@Entity
@NoArgsConstructor
@Data
public class AccessRoles {

  @Id
  @Column(name = "id")
  @NotNull
  private int id;

  @Column(name = "role")
  @NotNull
  private String role;

}
