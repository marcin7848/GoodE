package com.goode.business;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "exam_closed_answers")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ExamClosedAnswer {

  public interface ExamClosedAnswerValidation {}

  @Id
  @Column(name = "id_exam_closed_answer")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {ExamClosedAnswerValidation.class})
  private int id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_exam_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private ExamQuestion examQuestion;

  @Column(name = "closed_answer")
  @NotBlank(groups = {ExamClosedAnswerValidation.class})
  private String closedAnswer;

  @Column(name = "correct")
  @NotNull(groups = {ExamClosedAnswerValidation.class})
  private boolean correct;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "examClosedAnswer")
  private List<ExamAnswer> examAnswers = null;
}
