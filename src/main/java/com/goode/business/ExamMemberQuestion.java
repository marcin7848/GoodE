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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "exam_member_questions")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ExamMemberQuestion {

  public interface ExamMemberQuestionValidation{}

  @Id
  @Column(name = "id_exam_member_question")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {ExamMemberQuestionValidation.class})
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam_member")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private ExamMember examMember;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private ExamQuestion examQuestion;

  @Column(name = "position")
  private boolean position;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "examMemberQuestion")
  private List<Answer> answers = null;


}
