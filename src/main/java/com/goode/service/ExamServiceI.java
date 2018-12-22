package com.goode.service;

import com.goode.business.Exam;
import com.goode.business.ExamAnswer;
import com.goode.business.ExamAnswerWrapper;
import com.goode.business.ExamClosedAnswer;
import com.goode.business.ExamMember;
import com.goode.business.ExamMemberQuestion;
import com.goode.business.ExamQuestion;
import com.goode.business.Group;
import com.goode.business.Question;
import java.sql.Timestamp;
import java.util.List;

public interface ExamServiceI {

  ExamQuestion getExamQuestionById(int id);
  Exam getExamById(int id);
  Exam getExamFullById(int id);
  ExamMember getExamMemberByIdAccountAndIdExam(int id_account, int id_exam);
  ExamMember getExamMemberById(int id);
  ExamMemberQuestion getExamMemberQuestionById(int id);
  ExamClosedAnswer getExamClosedAnswerById(int id);
  ExamAnswer getExamAnswerByIdExamMemberQuestionAnIdExamClosedAnswer(int id_exam_member_question, int id_exam_closed_answer);
  Exam finishExam(Exam exam);
  List<Exam> getAllExamByIdGroup(Group group);
  Exam getRunningExamManagement(int id);
  Exam getRunningExam(int id);

  Exam addNew(Exam question);
  Exam edit(Exam exam);
  ExamQuestion addNewExamQuestion(Exam exam, Question question);
  void deleteExamQuestion(ExamQuestion examQuestion);
  boolean changePositionExamQuestion(ExamQuestion examQuestion, int newPosition);
  Exam initiateJoining(Exam exam);
  ExamMember joinToExam(Exam exam);
  void addAllExamQuestion(Exam exam, Group group);
  void blockExamMember(ExamMember examMember, String causeOfBlockade);
  Exam startExam(Exam exam, Timestamp finishTime);
  void addExamAnswer(Exam exam, List<ExamAnswerWrapper> examAnswerWrapper);


}
