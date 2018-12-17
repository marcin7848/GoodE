package com.goode.service;

import com.goode.business.Exam;

public interface ExamServiceI {

  Exam getExamById(int id);
  Exam addNew(Exam question);
  Exam edit(Exam exam);


}
