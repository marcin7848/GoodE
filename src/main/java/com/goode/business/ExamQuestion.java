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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "exam_questions")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ExamQuestion {

  public interface ExamQuestionValidation {}

  @Id
  @Column(name = "id_exam_question")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {ExamQuestionValidation.class})
  private int id;

  @Column(name = "question")
  @NotBlank(groups = {ExamQuestionValidation.class})
  private String question;

  @Column(name = "type")
  @NotNull(groups = {ExamQuestionValidation.class})
  @Min(value = 1, groups = {ExamQuestionValidation.class})
  @Max(value = 2, groups = {ExamQuestionValidation.class})
  private int type;

  @Column(name = "difficulty")
  @NotNull(groups = {ExamQuestionValidation.class})
  @Min(value = 1, groups = {ExamQuestionValidation.class})
  @Max(value = 10, groups = {ExamQuestionValidation.class})
  private int difficulty;

  @Column(name = "points")
  @NotNull(groups = {ExamQuestionValidation.class})
  @Min(value = 1, groups = {ExamQuestionValidation.class})
  @Max(value = 1000, groups = {ExamQuestionValidation.class})
  private int points;

  @Column(name = "answer_time")
  private int answerTime;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Exam exam;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "examQuestion")
  private List<ExamMemberQuestion> examMemberQuestions = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "examQuestion")
  private List<ExamClosedAnswer> examClosedAnswers = null;
}
