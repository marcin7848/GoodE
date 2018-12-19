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
import javax.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

@Table(name = "exams")
@Entity
@NoArgsConstructor
@Data
public class Exam {

  public interface ExamValidationFull{}
  public interface StartExamValidationFull{}

  @Id
  @Column(name = "id_exam")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @Column(name = "title")
  @NotBlank(groups = {ExamValidationFull.class})
  @Length(min = 4, max = 40, groups = {ExamValidationFull.class})
  @Pattern(regexp = "^[\\p{L}0-9_\\-\\/ +\\.]+$", groups = {ExamValidationFull.class})
  private String title;

  @Column(name = "password")
  @Length(max = 50, groups = {StartExamValidationFull.class})
  private String password;

  @Column(name = "color")
  @NotBlank(groups = {StartExamValidationFull.class})
  @Length(min = 7, max = 7, groups = {StartExamValidationFull.class})
  @Pattern(regexp = "^[\\p{L}0-9#]+$", groups = {StartExamValidationFull.class})
  private String color;

  @Column(name = "type")
  @NotNull(groups = {ExamValidationFull.class})
  @Min(value = 1, groups = {ExamValidationFull.class})
  @Max(value = 4, groups = {ExamValidationFull.class})
  private int type;

  @Column(name = "difficulty")
  @NotNull(groups = {ExamValidationFull.class})
  @Min(value = 1, groups = {ExamValidationFull.class})
  @Max(value = 10, groups = {ExamValidationFull.class})
  private int difficulty;

  @Column(name = "show_all_questions")
  @NotNull(groups = {ExamValidationFull.class})
  private boolean showAllQuestions;

  @Column(name = "return_to_questions")
  private boolean returnToQuestions;

  @Column(name = "send_results_instantly")
  @NotNull(groups = {ExamValidationFull.class})
  private boolean sendResultsInstantly;

  @Column(name = "show_full_results")
  @NotNull(groups = {ExamValidationFull.class})
  private boolean showFullResults;

  @Column(name = "mix_questions")
  private boolean mixQuestions;

  @Column(name = "max_time")
  private int maxTime;

  @Column(name = "additional_time")
  private int additionalTime;

  @Column(name = "number_of_questions")
  private int numberOfQuestions;

  @Column(name = "joining")
  private boolean joining;

  @Column(name = "started")
  private boolean started;

  @Column(name = "finished")
  private boolean finished;

  @Column(name = "rated")
  private boolean rated;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "finish_time")
  private Timestamp finishTime;

  @Column(name = "creation_time", updatable = false)
  @NotNull
  private Timestamp creationTime;

  @Column(name = "percent_to_pass")
  @NotNull(groups = {ExamValidationFull.class})
  private int percentToPass;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_group")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Group group;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "exam")
  private List<ExamQuestion> examQuestions = null;





}
