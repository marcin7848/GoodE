package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ClosedAnswer;
import com.goode.business.Exam;
import com.goode.business.ExamAnswer;
import com.goode.business.ExamAnswerWrapper;
import com.goode.business.ExamClosedAnswer;
import com.goode.business.ExamMember;
import com.goode.business.ExamMemberQuestion;
import com.goode.business.ExamQuestion;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.repository.ExamAnswerRepository;
import com.goode.repository.ExamClosedAnswerRepository;
import com.goode.repository.ExamMemberQuestionRepository;
import com.goode.repository.ExamMemberRepository;
import com.goode.repository.ExamQuestionRepository;
import com.goode.repository.ExamRepository;
import com.goode.repository.QuestionRepository;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExamService implements ExamServiceI {

  @Autowired
  AccountService accountService;

  @Autowired
  ExamRepository examRepository;

  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  ExamQuestionRepository examQuestionRepository;

  @Autowired
  ExamClosedAnswerRepository examClosedAnswerRepository;

  @Autowired
  ExamMemberRepository examMemberRepository;

  @Autowired
  ExamMemberQuestionRepository examMemberQuestionRepository;

  @Autowired
  ExamAnswerRepository examAnswerRepository;

  @Override
  public Exam getExamById(int id) {
    return examRepository.findExamById(id);
  }

  @Override
  public ExamQuestion getExamQuestionById(int id) {
    return examQuestionRepository.findExamQuestionById(id);
  }

  @Override
  public ExamMember getExamMemberByIdAccountAndIdExam(int id_account, int id_exam) {
    return examMemberRepository.findExamMemberByIdAccountAndIdExam(id_account, id_exam);
  }

  @Override
  public ExamMember getExamMemberById(int id) {
    return examMemberRepository.findExamMemberById(id);
  }

  @Override
  public ExamMemberQuestion getExamMemberQuestionById(int id) {
    return examMemberQuestionRepository.findExamMemberQuestionById(id);
  }

  @Override
  public ExamClosedAnswer getExamClosedAnswerById(int id) {
    return examClosedAnswerRepository.findExamClosedAnswerById(id);
  }

  @Override
  public ExamAnswer getExamAnswerByIdExamMemberQuestionAnIdExamClosedAnswer(int id_exam_member_question, int id_exam_closed_answer) {
    return examAnswerRepository.findExamAnswerByIdExamMemberQuestionAndIdExamClosedAnswer(id_exam_member_question, id_exam_closed_answer);
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

    if (exam == null) {
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
    exam.setPassword(oldExam.getPassword());

    exam = examRepository.save(exam);

    if (exam == null) {
      return null;
    }
    return exam;
  }

  @Override
  public ExamQuestion addNewExamQuestion(Exam exam, Question question) {
    ExamQuestion examQuestion = new ExamQuestion();
    examQuestion.setQuestion(question.getQuestion());
    examQuestion.setType(question.getType());
    examQuestion.setDifficulty(question.getDifficulty());
    examQuestion.setPoints(question.getPoints());
    examQuestion.setAnswerTime(question.getAnswerTime());
    examQuestion.setPosition(examRepository.getNextQuestionPosition(exam.getId()));
    examQuestion.setExam(exam);

    examQuestion = examQuestionRepository.save(examQuestion);
    if (examQuestion == null) {
      return null;
    }

    for (ClosedAnswer closedAnswer : question.getClosedAnswers()) {
      ExamClosedAnswer examClosedAnswer = new ExamClosedAnswer();
      examClosedAnswer.setClosedAnswer(closedAnswer.getClosedAnswer());
      examClosedAnswer.setCorrect(closedAnswer.isCorrect());
      examClosedAnswer.setExamQuestion(examQuestion);

      examClosedAnswerRepository.save(examClosedAnswer);
    }

    examQuestion.setExamClosedAnswers(
        examClosedAnswerRepository.findAllExamClosedAnswerByIdExamQuestion(examQuestion.getId()));

    return examQuestion;
  }

  @Override
  public void addAllExamQuestion(Exam exam, Group group) {
    List<Question> questions = questionRepository.findQuestionsByIdGroup(group.getId());
    for (Question q : questions) {
      this.addNewExamQuestion(exam, q);
    }
  }

  @Override
  public void deleteExamQuestion(ExamQuestion examQuestion) {
    examQuestionRepository.delete(examQuestion);
  }

  @Override
  public boolean changePositionExamQuestion(ExamQuestion examQuestion, int newPosition) {
    List<ExamQuestion> examQuestions = examQuestionRepository
        .findAllExamQuestionsByIdExam(examQuestion.getExam().getId());

    examQuestions.sort(Comparator.comparing(ExamQuestion::getPosition));
    examQuestions.remove(examQuestion.getPosition());
    examQuestion.setPosition(newPosition);
    examQuestions.add(examQuestion.getPosition(), examQuestion);
    int i = 0;
    for (ExamQuestion eq : examQuestions) {
      eq.setPosition(i);
      i++;
    }

    examQuestionRepository.saveAll(examQuestions);
    return true;
  }

  @Override
  public Exam initiateJoining(Exam exam) {
    exam.setJoining(true);
    exam = examRepository.save(exam);
    return exam;
  }

  @Override
  public ExamMember joinToExam(Exam exam) {
    Account loggedAccount = accountService.getLoggedAccount();
    ExamMember examMember = new ExamMember();
    examMember.setBlocked(false);
    examMember.setCauseOfBlockade(null);
    examMember.setPosition(0);
    examMember.setExam(exam);
    examMember.setAccount(loggedAccount);
    examMember = examMemberRepository.save(examMember);

    List<ExamQuestion> examQuestions = examQuestionRepository
        .findAllExamQuestionsByIdExam(exam.getId());

    if (exam.getType() == 1 || exam.getType() == 3) {
      int i = 0;
      for (ExamQuestion eq : examQuestions) {
        ExamMemberQuestion examMemberQuestion = new ExamMemberQuestion();
        examMemberQuestion.setPosition(i);
        examMemberQuestion.setExamMember(examMember);
        examMemberQuestion.setExamQuestion(eq);
        examMemberQuestionRepository.save(examMemberQuestion);
        i++;
      }
    }

    if (exam.getType() == 2) {
      for (int i = 0; i < exam.getNumberOfQuestions(); i++) {
        Collections.shuffle(examQuestions);
        ExamMemberQuestion examMemberQuestion = new ExamMemberQuestion();
        examMemberQuestion.setPosition(i);
        examMemberQuestion.setExamMember(examMember);
        examMemberQuestion.setExamQuestion(examQuestions.get(0));
        examMemberQuestionRepository.save(examMemberQuestion);
        examQuestions.remove(0);
      }
    }

    if (exam.getType() == 4) {
      int currentSummaryTimeQuestions = examQuestionRepository
          .getSummaryTimeForExamByIdExamMember(examMember.getId());
      int i = 0;
      while (currentSummaryTimeQuestions < exam.getMaxTime()) {
        Collections.shuffle(examQuestions);
        ExamMemberQuestion examMemberQuestion = new ExamMemberQuestion();
        examMemberQuestion.setPosition(i);
        examMemberQuestion.setExamMember(examMember);
        examMemberQuestion.setExamQuestion(examQuestions.get(0));
        examMemberQuestionRepository.save(examMemberQuestion);
        examQuestions.remove(0);
        currentSummaryTimeQuestions = examQuestionRepository
            .getSummaryTimeForExamByIdExamMember(examMember.getId());
      }

      /* czyszczenie, gdy przekracza czas, ale chyba za dużo czyściło i dla małych czasow moiże zostawać 1 pytanie np.
      while(currentSummaryTimeQuestions >= exam.getMaxTime()){
        List<ExamMemberQuestion> examMemberQuestions = examMemberQuestionRepository.findAllByIdExamMember(examMember.getId());
        Collections.shuffle(examMemberQuestions);
        examMemberQuestionRepository.delete(examMemberQuestions.get(0));
        currentSummaryTimeQuestions = examQuestionRepository.getSummaryTimeForExamByIdExamMember(examMember.getId());
      }
      */
    }

    if (exam.isMixQuestions()) {
      List<ExamMemberQuestion> examMemberQuestions = examMemberQuestionRepository
          .findAllByIdExamMember(examMember.getId());
      Collections.shuffle(examMemberQuestions);
      int i = 0;
      for (ExamMemberQuestion emq : examMemberQuestions) {
        emq.setPosition(i);
        examMemberQuestionRepository.save(emq);
        i++;
      }
    }

    return examMember;
  }

  @Override
  public void blockExamMember(ExamMember examMember, String causeOfBlockade) {
    examMember.setBlocked(true);
    examMember.setCauseOfBlockade(causeOfBlockade);
    examMemberRepository.save(examMember);
  }

  @Override
  public Exam startExam(Exam exam, Timestamp finishTime) {
    exam.setStarted(true);
    exam.setStartTime(new Timestamp(new Date().getTime()));

    if (exam.getType() == 1 || exam.getType() == 2) {
      exam.setFinishTime(finishTime);
    } else {
      List<ExamMember> examMembers = examMemberRepository.findExamMembersByIdExam(exam.getId());
      int max = 0;
      for (ExamMember em : examMembers) {
        int summaryTime = examQuestionRepository.getSummaryTimeForExamByIdExamMember(em.getId());
        if (max < summaryTime) {
          max = summaryTime;
        }
      }

      Timestamp timestamp = new Timestamp(new Date().getTime() + max * 1000 + 2000);
      exam.setFinishTime(timestamp);
    }

    exam = examRepository.save(exam);
    return exam;
  }


  @Override
  public void addExamAnswer(Exam exam, List<ExamAnswerWrapper> examAnswerWrapper) {
    Account loggedAccount = accountService.getLoggedAccount();
    for(ExamAnswerWrapper eaw: examAnswerWrapper) {
      ExamMemberQuestion examMemberQuestion = examMemberQuestionRepository
          .findExamMemberQuestionById(eaw.getId_exam_member_question());

      if(loggedAccount.getId() != examMemberQuestion.getExamMember().getAccount().getId()){
        continue;
      }

      List<ExamAnswer> examAnswers = examAnswerRepository
          .findExamAnswersByIdExamMemberQuestion(examMemberQuestion.getId());
      examAnswerRepository.deleteAll(examAnswers);

      List<ExamClosedAnswer> examClosedAnswers = new ArrayList<>();
      for(Integer listIdExamClosedAnswer: eaw.getId_exam_closed_answers()){
        ExamClosedAnswer examClosedAnswer = examClosedAnswerRepository.findExamClosedAnswerByIdAndIdExamQuestion(listIdExamClosedAnswer, examMemberQuestion.getExamQuestion().getId());
        if(examClosedAnswer != null){
          examClosedAnswers.add(examClosedAnswer);
        }
      }

      if(!examClosedAnswers.isEmpty()){
        for(ExamClosedAnswer eca: examClosedAnswers) {
          ExamAnswer examAnswer = new ExamAnswer();
          examAnswer.setExamMemberQuestion(examMemberQuestion);
          examAnswer.setExamClosedAnswer(eca);
          examAnswerRepository.save(examAnswer);
        }
      }
    }
  }

  @Override
  public Exam finishExam(Exam exam) {
    exam.setJoining(true);
    exam.setStarted(true);
    exam.setFinished(true);
    exam.setFinishTime(new Timestamp(new Date().getTime()));

    if(exam.isSendResultsInstantly()){
      exam.setRated(true);
    }

    exam = examRepository.save(exam);
    return exam;
  }



}
