package com.goode.business;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "activation_codes")
@Entity
@NoArgsConstructor
@Data
public class ActivationCode {

  @Id
  @Column(name = "id")
  @NotNull
  private int id;

  @Column(name = "id_account")
  @NotNull
  private int id_account;

  @Column(name = "type")
  @NotNull
  private int type;

  @Column(name = "code")
  @NotNull
  private String code;

}
