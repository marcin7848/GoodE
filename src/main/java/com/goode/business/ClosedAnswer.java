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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "closed_answers")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ClosedAnswer {

  public interface ClosedAnswerValidation {}

  @Id
  @Column(name = "id_closed_answer")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {ClosedAnswerValidation.class})
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Question question;

  @Column(name = "closed_answer")
  @NotBlank(groups = {ClosedAnswerValidation.class})
  private String closedAnswer;

  @Column(name = "correct")
  @NotNull(groups = {ClosedAnswerValidation.class})
  private boolean correct;

}
