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

@Table(name = "activation_codes")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ActivationCode {

  @Id
  @Column(name = "id_activation_code")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_account")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Account account;

  @Column(name = "type")
  @NotNull
  private int type;

  @Column(name = "code")
  @NotNull
  private String code;

}
