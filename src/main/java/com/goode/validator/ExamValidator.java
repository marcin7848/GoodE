package com.goode.validator;

import com.goode.ErrorCode;
import com.goode.business.Exam;
import org.springframework.stereotype.Component;

@Component
public class ExamValidator extends BaseValidator{


  public boolean validateExamData(Exam exam, ErrorCode errorCode){

    if(!exam.isShowAllQuestions() && !exam.isReturnToQuestions()){
      errorCode.rejectValue("isReturnToQuestions", "error.exam.isReturnToQuestions.cannotBeNull");
      return false;
    }

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

}
