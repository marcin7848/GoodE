package com.goode.service;

import com.goode.business.ClosedAnswer;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.business.QuestionGroup;
import com.goode.repository.ClosedAnswerRepository;
import com.goode.repository.QuestionGroupRepository;
import com.goode.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService implements QuestionServiceI {

  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  QuestionGroupRepository questionGroupRepository;

  @Autowired
  ClosedAnswerRepository closedAnswerRepository;

  @Override
  public Question getQuestionById(int id) {
    return questionRepository.findQuestionById(id);
  }


  @Override
  public QuestionGroup getQuestionGroupByQuestionAndGroup(Question question, Group group) {
    return questionGroupRepository.findQuestionGroupByQuestionAndGroup(question, group);
  }

  @Override
  public Question addNew(Question question, Group group) {

    question = questionRepository.save(question);

    if(question == null){
      return null;
    }

    QuestionGroup questionGroup = new QuestionGroup();
    questionGroup.setQuestion(question);
    questionGroup.setGroup(group);
    questionGroup = questionGroupRepository.save(questionGroup);

    if(questionGroup == null){
      return null;
    }
    return question;
  }

  @Override
  public Question edit(Question question, Group group) {

    QuestionGroup questionGroup = questionGroupRepository.findQuestionGroupByQuestionAndGroup(question, group);
    questionGroupRepository.delete(questionGroup);

    question = questionRepository.save(question);

    if(question == null){
      return null;
    }

    questionGroup = new QuestionGroup();
    questionGroup.setQuestion(question);
    questionGroup.setGroup(group);
    questionGroup = questionGroupRepository.save(questionGroup);

    if(questionGroup == null){
      return null;
    }
    return question;
  }

  @Override
  public void delete(Question question, Group group) {

    QuestionGroup questionGroup = questionGroupRepository.findQuestionGroupByQuestionAndGroup(question, group);
    if(questionGroup == null){
      return;
    }

    questionRepository.delete(question);
  }


  @Override
  public ClosedAnswer addNewClosedAnswer(ClosedAnswer closedAnswer) {
    closedAnswer = closedAnswerRepository.save(closedAnswer);
    return closedAnswer;
  }

}
