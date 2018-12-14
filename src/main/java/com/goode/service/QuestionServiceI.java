package com.goode.service;

import com.goode.business.Group;
import com.goode.business.Question;

public interface QuestionServiceI {

  Question getQuestionById(int id);
  Question addNew(Question question, Group group);
  Question edit(Question question, Group group);
  void delete(Question question, Group group);

}
