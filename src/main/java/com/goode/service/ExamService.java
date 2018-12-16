package com.goode.service;

import com.goode.business.Exam;
import com.goode.repository.ExamRepository;
import java.sql.Timestamp;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamService implements ExamServiceI {

  @Autowired
  ExamRepository examRepository;

  @Override
  public Exam addNew(Exam exam) {
    exam.setCreationTime(new Timestamp(new Date().getTime()));

    exam = examRepository.save(exam);

    if(exam == null){
      return null;
    }
    return exam;
  }




}
