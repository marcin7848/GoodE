package com.goode.business;

import com.goode.business.Group.AddNewValidation;
import com.goode.business.Group.FullValidation;
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

  @Id
  @Column(name = "id_closed_answer")
  @GeneratedValue(strategy = GenerationType.AUTO)
  @NotNull
  private int id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "id_question")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private Question question;

  @Column(name = "closed_answer")
  @NotBlank
  private String closedAnswer;

  @Column(name = "correct")
  @NotNull
  private boolean correct;

}
