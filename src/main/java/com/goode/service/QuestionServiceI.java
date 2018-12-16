package com.goode.service;

import com.goode.business.ClosedAnswer;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.business.QuestionGroup;
import java.util.List;

public interface QuestionServiceI {

  Question getQuestionById(int id);
  List<Question> getQuestionsByIdGroup(int id_group);
  Question addNew(Question question, Group group);
  Question edit(Question question, Group group);
  void delete(Question question, Group group);

  ClosedAnswer getClosedAnswerById(int id);
  QuestionGroup getQuestionGroupByQuestionAndGroup(Question question, Group group);
  ClosedAnswer addNewClosedAnswer(ClosedAnswer closedAnswer);
  ClosedAnswer editClosedAnswer(ClosedAnswer closedAnswer);
  void deleteClosedAnswer(ClosedAnswer closedAnswer);
  void changeCorrectClosedAnswer(ClosedAnswer closedAnswer);



}
