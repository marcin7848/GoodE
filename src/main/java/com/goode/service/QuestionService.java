package com.goode.service;

import com.goode.business.Question;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements QuestionServiceI, StandardizeService<Question> {

  @Override
  public Question addNew(Question question) {
    return new Question();
  }

  @Override
  public Question edit(Question question) {
    return new Question();
  }


  @Override
  public void delete(Question question) {

  }
}
