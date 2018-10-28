package com.goode.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Table(name = "groups")
@Entity
@NoArgsConstructor
@Data
public class Group {

  public interface FullValidation {}
  public interface AddNewValidation {}

  @Id
  @Column(name = "id_group")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "name")
  @NotBlank(groups = {FullValidation.class, AddNewValidation.class})
  @Length(min = 4, max = 40, groups = {FullValidation.class, AddNewValidation.class})
  @Pattern(regexp = "^[\\p{L}0-9_\\-\\/ +]+$", groups = {FullValidation.class, AddNewValidation.class})
  private String name;

  @Column(name = "description")
  @Length(max = 100, groups = {FullValidation.class, AddNewValidation.class})
  private String description;

  @Column(name = "password")
  @Length(max = 15, groups = {FullValidation.class, AddNewValidation.class})
  private String password;

  @Column(name = "possible_to_join")
  @NotNull(groups = {FullValidation.class, AddNewValidation.class})
  private boolean possibleToJoin;

  @Column(name = "acceptance")
  @NotNull(groups = {FullValidation.class, AddNewValidation.class})
  private boolean acceptance;

  @Column(name = "hidden")
  @NotNull(groups = {FullValidation.class, AddNewValidation.class})
  private boolean hidden;

  @Column(name = "id_group_parent")
  private int idGroupParent;

  @Column(name = "order")
  @NotNull(groups = {FullValidation.class})
  private int order;

  @Column(name = "creation_time", updatable = false)
  @NotNull(groups = {FullValidation.class})
  private Timestamp creationTime;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
  @ToString.Exclude
  @JsonIgnore
  private List<GroupMember> groupMembers;
}
