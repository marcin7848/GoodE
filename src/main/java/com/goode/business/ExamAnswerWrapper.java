package com.goode.business;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExamAnswerWrapper {
  private int id_exam_member_question;
  private List<Integer> id_exam_closed_answers;
}
