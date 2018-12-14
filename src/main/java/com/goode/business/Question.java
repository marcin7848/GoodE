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

  @Id
  @Column(name = "id_question")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
  private List<QuestionGroup> questionGroups = null;

  @Column(name = "question")
  @NotBlank
  private String question;

  @Column(name = "answer_time")
  private Timestamp answerTime;

  @Column(name = "difficulty")
  @NotNull
  private int difficulty;

  @Column(name = "points")
  @NotNull
  private int points;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "question")
  private List<ClosedAnswer> closedAnswers = null;
}
