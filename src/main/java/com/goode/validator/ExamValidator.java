package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.Exam;
import com.goode.business.ExamClosedAnswer;
import com.goode.business.ExamQuestion;
import com.goode.repository.ExamClosedAnswerRepository;
import com.goode.repository.ExamQuestionRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExamValidator extends BaseValidator{

  @Autowired
  ExamQuestionRepository examQuestionRepository;

  @Autowired
  ExamClosedAnswerRepository examClosedAnswerRepository;

  public boolean validateExamData(Exam exam, ErrorCode errorCode){

    if(exam.getType() == 2 && exam.getNumberOfQuestions() < 1){
      errorCode.rejectValue("numberOfQuestions", "error.exam.numberOfQuestions.cannotBe0");
      return false;
    }

    if(exam.getType() == 4 && exam.getMaxTime() < 1){
      errorCode.rejectValue("maxTime", "error.exam.maxTime.cannotBe0");
      return false;
    }

    return true;
  }

  public boolean validateInitiateJoiningExam(Exam exam, ErrorCode errorCode){
    List<ExamQuestion> examQuestions = examQuestionRepository.findAllExamQuestionsByIdExam(exam.getId());
    if(examQuestions.size() < 2){
      errorCode.rejectValue("numberOfQuestions", "error.exam.joining.examQuestions.needsMoreThan2");
      return false;
    }

    for(ExamQuestion eq: examQuestions) {
      List<ExamClosedAnswer> examClosedAnswers = examClosedAnswerRepository.findAllExamClosedAnswerByIdExamQuestion(eq.getId());
      if(examClosedAnswers.size() < 2){
        errorCode.rejectValue("examClosedAnswers", "error.exam.joining.examClosedAnswers.needsMoreThan2");
        return false;
      }
    }

    if(exam.getType() == 2 && exam.getNumberOfQuestions() == 0){
      errorCode.rejectValue("numberOfQuestions", "error.exam.joining.numberOfQuestions.moreThan0");
      return false;
    }

    if(exam.getType() == 2 && examQuestions.size() < exam.getNumberOfQuestions()){
      errorCode.rejectValue("numberOfQuestions", "error.exam.joining.examQuestions.notEnough");
      return false;
    }

    if(exam.getType() == 3){
      for(ExamQuestion eq: examQuestions) {
          if(eq.getAnswerTime() < 1){
            errorCode.rejectValue("answerTime", "error.exam.joining.answerTime.moreThan15");
            return false;
          }
      }
    }

    if(exam.getType() == 4 && exam.getMaxTime() < 5){
      errorCode.rejectValue("maxTime", "error.exam.joining.maxTime.moreThan30");
      return false;
    }

    return true;
  }

}
