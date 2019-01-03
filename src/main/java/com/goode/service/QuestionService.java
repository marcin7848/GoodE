package com.goode.service;

import com.goode.business.ClosedAnswer;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.business.QuestionGroup;
import com.goode.repository.ClosedAnswerRepository;
import com.goode.repository.QuestionGroupRepository;
import com.goode.repository.QuestionRepository;
import java.util.List;
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
  public ClosedAnswer getClosedAnswerById(int id) {
    return closedAnswerRepository.findClosedAnswerById(id);
  }

  @Override
  public Question getQuestionById(int id) {
    return questionRepository.findQuestionById(id);
  }

  @Override
  public QuestionGroup getQuestionGroupByQuestionAndGroup(Question question, Group group) {
    return questionGroupRepository.findQuestionGroupByQuestionAndGroup(question, group);
  }

  @Override
  public List<Question> getQuestionsByIdGroup(int id_group) {
    List<Question> questions = questionRepository.findQuestionsByIdGroup(id_group);
    for(int i=0; i<questions.size(); i++){
      questions.get(i).setQuestionGroups(null);
      List<ClosedAnswer> closedAnswers = questions.get(i).getClosedAnswers();
      for(int j=0; j<closedAnswers.size(); j++){
        closedAnswers.get(j).setQuestion(null);
      }
    }

    return questions;
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

  @Override
  public ClosedAnswer editClosedAnswer(ClosedAnswer closedAnswer) {
    closedAnswer = closedAnswerRepository.save(closedAnswer);
    return closedAnswer;
  }

  @Override
  public void deleteClosedAnswer(ClosedAnswer closedAnswer) {
    closedAnswerRepository.delete(closedAnswer);
  }

  @Override
  public void changeCorrectClosedAnswer(ClosedAnswer closedAnswer) {
    closedAnswer.setCorrect(!closedAnswer.isCorrect());
    closedAnswerRepository.save(closedAnswer);
  }
}
