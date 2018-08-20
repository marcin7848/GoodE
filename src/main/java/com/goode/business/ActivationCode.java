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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Table(name = "activation_codes")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ActivationCode {

  public static final int TYPE_ACTIVATION_ACCOUNT_CODE = 1;

  @Id
  @Column(name = "id_activation_code")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_account")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Account account;

  @Column(name = "type")
  @NotNull
  private int type;
  //1 - for activation account (enabled -> true)

  @Column(name = "code")
  @NotNull
  @Length(max = 50)
  private String code;

}
