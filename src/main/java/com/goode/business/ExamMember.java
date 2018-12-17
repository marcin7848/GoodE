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
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "exam_members")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ExamMember {

  public interface ExamMemberValidation{}

  @Id
  @Column(name = "id_exam_member")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull(groups = {ExamMemberValidation.class})
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_exam")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Exam exam;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "examMember")
  private List<Account> accounts = null;

  @Column(name = "blocked")
  private boolean blocked;

  @Column(name = "cause_of_blockade")
  private String causeOfBlockade;

  @Column(name = "position")
  private int position;

  @Column(name = "creation_time", updatable = false)
  @NotNull
  private Timestamp creationTime;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "examMember")
  private List<ExamMemberQuestion> examMemberQuestions = null;

}
