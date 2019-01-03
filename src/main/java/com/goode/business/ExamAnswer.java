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
@Table(name = "exam_answers")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ExamAnswer {

  @Id
  @Column(name = "id_exam_answer")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam_member_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private ExamMemberQuestion examMemberQuestion;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam_closed_answer")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ExamClosedAnswer examClosedAnswer;

}
