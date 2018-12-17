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
  public Exam getExamById(int id){
    return examRepository.findExamById(id);
  }


  @Override
  public Exam addNew(Exam exam) {
    exam.setCreationTime(new Timestamp(new Date().getTime()));
    exam.setStartTime(null);
    exam.setFinishTime(null);
    exam.setStarted(false);
    exam.setFinished(false);
    exam.setRated(false);

    exam = examRepository.save(exam);

    if(exam == null){
      return null;
    }
    return exam;
  }

  @Override
  public Exam edit(Exam exam) {
    Exam oldExam = examRepository.findExamById(exam.getId());
    exam.setCreationTime(oldExam.getCreationTime());
    exam.setColor(oldExam.getColor());
    exam.setStartTime(oldExam.getStartTime());
    exam.setFinishTime(oldExam.getFinishTime());
    exam.setStarted(oldExam.isStarted());
    exam.setFinished(oldExam.isFinished());
    exam.setRated(oldExam.isRated());

    exam = examRepository.save(exam);

    if(exam == null){
      return null;
    }
    return exam;
  }


}
