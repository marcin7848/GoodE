package com.goode.business;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Question {

  public interface QuestionValidation {}

  @Id
  @Column(name = "id_question")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {QuestionValidation.class})
  private int id;

  @Column(name = "question")
  @NotBlank(groups = {QuestionValidation.class})
  private String question;

  @Column(name = "type")
  @NotNull(groups = {QuestionValidation.class})
  @Min(value = 1, groups = {QuestionValidation.class})
  @Max(value = 2, groups = {QuestionValidation.class})
  private int type;

  @Column(name = "difficulty")
  @NotNull(groups = {QuestionValidation.class})
  @Min(value = 1, groups = {QuestionValidation.class})
  @Max(value = 10, groups = {QuestionValidation.class})
  private int difficulty;

  @Column(name = "points")
  @NotNull(groups = {QuestionValidation.class})
  @Min(value = 1, groups = {QuestionValidation.class})
  @Max(value = 1000, groups = {QuestionValidation.class})
  private int points;

  @Column(name = "answer_time")
  private int answerTime;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
  private List<QuestionGroup> questionGroups = null;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "question")
  private List<ClosedAnswer> closedAnswers = null;
}
