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

@Entity
@Table(name = "question_groups")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class QuestionGroup {

  @Id
  @Column(name = "id_question_group")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_group")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Group group;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Question question;

}
