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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Account {

  public static final int MAIN_ADMINISTRATOR_ID = 1; //for safety, block some actions e.g. change accessRole for main admin

  public interface ValidationStepOne {}

  public interface ValidationStepTwo {}

  @Id
  @Column(name = "id_account")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "username")
  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Length(min = 6, max = 15, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private String username;

  @Column(name = "email")
  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Length(min = 6, max = 100, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Pattern(regexp = "^[a-zA-Z0-9._-]+@([a-zA-Z0-9-_]+\\.)+[a-zA-Z0-9-_]+$", groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private String email;

  @Column(name = "password")
  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Length(min = 8, max = 100, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private String password;

  @Column(name = "register_no")
  @NotNull(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Min(value = 1, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private Integer register_no;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_access_role")
  @OnDelete(action = OnDeleteAction.NO_ACTION)
  @NotNull(groups = {ValidationStepTwo.class})
  private AccessRole accessRole;

  @Column(name = "enabled")
  @NotNull(groups = {ValidationStepTwo.class})
  private boolean enabled;

  @Column(name = "firstname")
  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Length(min = 2, max = 30, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Pattern(regexp = "^[a-zA-Z-]+$", groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private String firstName;

  @Column(name = "lastname")
  @NotBlank(groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Length(min = 2, max = 30, groups = {ValidationStepOne.class, ValidationStepTwo.class})
  @Pattern(regexp = "^[a-zA-Z-]+$", groups = {ValidationStepOne.class, ValidationStepTwo.class})
  private String lastName;

  @Column(name = "creation_time", updatable = false)
  @NotNull(groups = {ValidationStepTwo.class})
  private Timestamp creationTime;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
  @ToString.Exclude
  private List<ActivationCode> activationCodes;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
  @ToString.Exclude
  private List<GroupMember> groupMembers;

}