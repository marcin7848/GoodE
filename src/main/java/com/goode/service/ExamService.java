package com.goode.service;

import com.goode.business.Account;
import com.goode.business.ClosedAnswer;
import com.goode.business.Exam;
import com.goode.business.ExamAnswer;
import com.goode.business.ExamAnswerWrapper;
import com.goode.business.ExamClosedAnswer;
import com.goode.business.ExamMember;
import com.goode.business.ExamMemberQuestion;
import com.goode.business.ExamMemberQuestionResult;
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
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
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
  public List<Exam> getAllExamByIdGroup(Group group) {
    List<Exam> exams = examRepository.findAllExamsByGroup(group);
    for (int i = 0; i < exams.size(); i++) {
      exams.get(i).setExamQuestions(null);
      exams.get(i).setGroup(null);
      exams.get(i).setPassword(null);
      exams.get(i).setColor(null);
      exams.get(i).setExamMembers(null);
    }
    return exams;
  }

  @Override
  public Exam getExamFullById(int id) {
    Exam exam = this.getExamById(id);
    int idGroup = exam.getGroup().getId();
    exam.setExamMembers(null);
    exam.setGroup(null);
    Group group1 = new Group();
    group1.setId(idGroup);
    exam.setGroup(group1);
    exam.getExamQuestions().sort(Comparator.comparing(ExamQuestion::getPosition));

    for (int i = 0; i < exam.getExamQuestions().size(); i++) {
      exam.getExamQuestions().get(i).setExam(null);
      exam.getExamQuestions().get(i).setExamMemberQuestions(null);
      for (int j = 0; j < exam.getExamQuestions().get(i).getExamClosedAnswers().size(); j++) {
        exam.getExamQuestions().get(i).getExamClosedAnswers().get(j).setExamQuestion(null);
        exam.getExamQuestions().get(i).getExamClosedAnswers().get(j).setExamAnswers(null);
      }
      exam.getExamQuestions().get(i).getExamClosedAnswers().sort(Comparator.comparing(ExamClosedAnswer::getId));
    }
    return exam;
  }

  @Override
  public int[] getResultsExam(Exam exam, List<ExamMemberQuestionResult> examMemberQuestionResults, Account account) {
    int[] results = new int[2];
    int maxPoints = 0;
    int points = 0;

    ExamMember examMember = examMemberRepository
        .findExamMemberByIdAccountAndIdExam(account.getId(), exam.getId());
    if (examMember == null) {
      return results;
    }

    for (ExamMemberQuestion examMemberQuestion : examMember.getExamMemberQuestions()) {
      ExamMemberQuestionResult examMemberQuestionResult = new ExamMemberQuestionResult();
      examMemberQuestionResult.setIdExamMemberQuestion(examMemberQuestion.getId());
      examMemberQuestionResult.setPoints(0);

      maxPoints += examMemberQuestion.getExamQuestion().getPoints();

      List<ExamClosedAnswer> examClosedAnswers = new ArrayList<>();
      for (ExamClosedAnswer examClosedAnswer : examMemberQuestion.getExamQuestion()
          .getExamClosedAnswers()) {
        if (examClosedAnswer.isCorrect()) {
          examClosedAnswers.add(examClosedAnswer);
        }
      }

      List<ExamAnswer> examAnswers = examMemberQuestion.getExamAnswers();

      if (examClosedAnswers.isEmpty()) {
        if (!examAnswers.isEmpty() && examAnswers.size() == 1
            && examAnswers.get(0).getExamClosedAnswer() == null) {
          points += examMemberQuestion.getExamQuestion().getPoints();
          examMemberQuestionResult.setPoints(examMemberQuestion.getExamQuestion().getPoints());
          examMemberQuestionResults.add(examMemberQuestionResult);
          continue;
        } else {
          examMemberQuestionResults.add(examMemberQuestionResult);
          continue;
        }
      }

      if (examClosedAnswers.size() != examAnswers.size()) {
        examMemberQuestionResults.add(examMemberQuestionResult);
        continue;
      }

      int existAnswers = 0;
      for (ExamClosedAnswer examClosedAnswer : examClosedAnswers) {
        for (ExamAnswer examAnswer : examAnswers) {
          if (examClosedAnswer.getId() == examAnswer.getExamClosedAnswer().getId()) {
            existAnswers += 1;
            break;
          }
        }
      }

      if (existAnswers == examClosedAnswers.size()) {
        points += examMemberQuestion.getExamQuestion().getPoints();
        examMemberQuestionResult.setPoints(examMemberQuestion.getExamQuestion().getPoints());
      }

      examMemberQuestionResults.add(examMemberQuestionResult);
    }

    results[0] = maxPoints;
    results[1] = points;

    return results;
  }

  @Override
  public JSONArray getResultsForAllExamMembers(Exam exam) {

    List<ExamMember> examMembers = examMemberRepository.findExamMembersByIdExam(exam.getId());
    JSONArray jsonArray = new JSONArray();

    for(ExamMember examMember: examMembers)
    {
      List<ExamMemberQuestionResult> examMemberQuestionResults = new ArrayList<>();
      int[] results = this.getResultsExam(exam, examMemberQuestionResults, examMember.getAccount());
      JSONObject obj = new JSONObject();
      obj.put("maxPoints", results[0]);
      obj.put("points", results[1]);
      obj.put("idExamMember", examMember.getId());
      obj.put("exam", this.getExamMemberResults(exam.getId(),  examMember.getAccount()));
      obj.put("examMemberQuestionResults", examMemberQuestionResults);
      jsonArray.put(obj);
    }

    return jsonArray;
  }


  @Override
  public Exam getRunningExamManagement(int id) {
    Exam exam = this.getExamById(id);
    exam.setGroup(null);
    exam.setExamQuestions(null);

    for (int i = 0; i < exam.getExamMembers().size(); i++) {
      exam.getExamMembers().get(i).setExam(null);
      exam.getExamMembers().get(i).getAccount().setAccessRole(null);
      exam.getExamMembers().get(i).getAccount().setActivationCodes(null);
      exam.getExamMembers().get(i).getAccount().setGroupMembers(null);
      exam.getExamMembers().get(i).getAccount().setExamMembers(null);
      exam.getExamMembers().get(i).getAccount().setPassword("");

      for (int j = 0; j < exam.getExamMembers().get(i).getExamMemberQuestions().size(); j++) {
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).setExamMember(null);
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).setExamAnswers(null);
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
            .setExam(null);
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
            .setExamMemberQuestions(null);
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
            .setExamClosedAnswers(null);
      }
    }

    return exam;
  }

  @Override
  public Exam getRunningExam(int id) {
    Account loggedAccount = accountService.getLoggedAccount();
    Exam exam = this.getExamById(id);
    exam.setGroup(null);
    exam.setExamQuestions(null);
    exam.setPassword("");

    ExamMember examMember = new ExamMember();

    for (int i = 0; i < exam.getExamMembers().size(); i++) {
      if (exam.getExamMembers().get(i).getAccount().getId() == loggedAccount.getId()) {
        examMember = exam.getExamMembers().get(i);
        examMember.setExam(null);
        examMember.setExamMemberQuestions(null);
        examMember.getAccount().setPassword("");
        examMember.getAccount().setExamMembers(null);
        examMember.getAccount().setGroupMembers(null);
        examMember.getAccount().setActivationCodes(null);
        examMember.getAccount().getAccessRole().setAccounts(null);
        examMember.getAccount().getAccessRole().setGroupMembers(null);

        //do usuniecia drugi warunek w ifie poniżej, jeśli ma niezwracać wszystkich pytań na raz
        if (exam.isShowAllQuestions() || !exam.isShowAllQuestions()) {
          List<ExamMemberQuestion> examMemberQuestions = examMemberQuestionRepository
              .findAllByIdExamMember(examMember.getId());
          for (int j = 0; j < examMemberQuestions.size(); j++) {
            examMemberQuestions.get(j).setExamMember(null);
            examMemberQuestions.get(j).getExamQuestion().setExam(null);
            examMemberQuestions.get(j).getExamQuestion().setExamMemberQuestions(null);
            for (int k = 0;
                k < examMemberQuestions.get(j).getExamQuestion().getExamClosedAnswers().size();
                k++) {
              examMemberQuestions.get(j).getExamQuestion().getExamClosedAnswers().get(k)
                  .setExamAnswers(null);
              examMemberQuestions.get(j).getExamQuestion().getExamClosedAnswers().get(k)
                  .setExamQuestion(null);
              examMemberQuestions.get(j).getExamQuestion().getExamClosedAnswers().get(k)
                  .setCorrect(false);
            }

            for (int g = 0; g < examMemberQuestions.get(j).getExamAnswers().size(); g++) {
              examMemberQuestions.get(j).getExamAnswers().get(g).setExamMemberQuestion(null);
              examMemberQuestions.get(j).getExamAnswers().get(g).getExamClosedAnswer()
                  .setExamQuestion(null);
              examMemberQuestions.get(j).getExamAnswers().get(g).getExamClosedAnswer()
                  .setExamAnswers(null);
            }
          }

          examMember.setExamMemberQuestions(examMemberQuestions);

        } else {
          ExamMemberQuestion examMemberQuestion = examMemberQuestionRepository
              .findExamMemberQuestionByIdExamMemberAndPosition(examMember.getId(),
                  examMember.getPosition());
          examMemberQuestion.setExamMember(null);
          examMemberQuestion.getExamQuestion().setExam(null);
          examMemberQuestion.getExamQuestion().setExamMemberQuestions(null);
          for (int k = 0; k < examMemberQuestion.getExamQuestion().getExamClosedAnswers().size();
              k++) {
            examMemberQuestion.getExamQuestion().getExamClosedAnswers().get(k).setExamAnswers(null);
            examMemberQuestion.getExamQuestion().getExamClosedAnswers().get(k)
                .setExamQuestion(null);
            examMemberQuestion.getExamQuestion().getExamClosedAnswers().get(k).setCorrect(false);
          }

          for (int g = 0; g < examMemberQuestion.getExamAnswers().size(); g++) {
            examMemberQuestion.getExamAnswers().get(g).setExamMemberQuestion(null);
            examMemberQuestion.getExamAnswers().get(g).getExamClosedAnswer().setExamQuestion(null);
            examMemberQuestion.getExamAnswers().get(g).getExamClosedAnswer().setExamAnswers(null);
          }

          List<ExamMemberQuestion> examMemberQuestions = new ArrayList<>();
          examMemberQuestions.add(examMemberQuestion);
          examMember.setExamMemberQuestions(examMemberQuestions);

        }
        break;
      }
    }

    List<ExamMember> examMembers = new ArrayList<>();
    examMembers.add(examMember);
    exam.setExamMembers(examMembers);

    return exam;
  }

  @Override
  public Exam getAllExamMembersResults(int id) {
    Exam exam = this.getExamById(id);
    exam.setGroup(null);
    exam.setExamQuestions(null);
    exam.setPassword("");

    for (int i = 0; i < exam.getExamMembers().size(); i++) {
      exam.getExamMembers().get(i).getAccount().setAccessRole(null);
      exam.getExamMembers().get(i).getAccount().setActivationCodes(null);
      exam.getExamMembers().get(i).getAccount().setGroupMembers(null);
      exam.getExamMembers().get(i).getAccount().setExamMembers(null);
      exam.getExamMembers().get(i).setExam(null);

      exam.getExamMembers().get(i).getExamMemberQuestions()
          .sort(Comparator.comparing(ExamMemberQuestion::getPosition));

      for (int j = 0; j < exam.getExamMembers().get(i).getExamMemberQuestions().size(); j++) {
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).setExamMember(null);

        for (int k = 0;
            k < exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers()
                .size(); k++) {
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
              .setExamMemberQuestion(null);
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
              .getExamClosedAnswer().setExamAnswers(null);
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
              .getExamClosedAnswer().setExamQuestion(null);
        }

        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
            .setExam(null);
        exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
            .setExamMemberQuestions(null);

        for (int g = 0;
            g < exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
                .getExamClosedAnswers().size(); g++) {
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
              .getExamClosedAnswers().get(g).setExamQuestion(null);
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
              .getExamClosedAnswers().get(g).setExamAnswers(null);
        }
      }
    }

    return exam;
  }

  @Override
  public Exam getExamMemberResults(int id, Account account) {
    Exam exam = this.getExamById(id);
    exam.setGroup(null);
    exam.setExamQuestions(null);
    exam.setPassword("");

    ExamMember examMember = new ExamMember();

    for (int i = 0; i < exam.getExamMembers().size(); i++) {
      if (exam.getExamMembers().get(i).getAccount() == account) {
        exam.getExamMembers().get(i).getAccount().setAccessRole(null);
        exam.getExamMembers().get(i).getAccount().setActivationCodes(null);
        exam.getExamMembers().get(i).getAccount().setGroupMembers(null);
        exam.getExamMembers().get(i).getAccount().setExamMembers(null);
        exam.getExamMembers().get(i).setExam(null);

        exam.getExamMembers().get(i).getExamMemberQuestions()
            .sort(Comparator.comparing(ExamMemberQuestion::getPosition));

        for (int j = 0; j < exam.getExamMembers().get(i).getExamMemberQuestions().size(); j++) {
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).setExamMember(null);

          for (int k = 0;
              k < exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers()
                  .size(); k++) {
            exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
                .setExamMemberQuestion(null);
            exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
                .getExamClosedAnswer().setExamAnswers(null);
            exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamAnswers().get(k)
                .getExamClosedAnswer().setExamQuestion(null);
          }

          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
              .setExam(null);
          exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
              .setExamMemberQuestions(null);

          for (int g = 0;
              g < exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
                  .getExamClosedAnswers().size(); g++) {
            exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
                .getExamClosedAnswers().get(g).setExamQuestion(null);
            exam.getExamMembers().get(i).getExamMemberQuestions().get(j).getExamQuestion()
                .getExamClosedAnswers().get(g).setExamAnswers(null);
          }
        }
        examMember = exam.getExamMembers().get(i);
        break;
      }
    }

    List<ExamMember> examMembers = new ArrayList<>();
    examMembers.add(examMember);
    exam.setExamMembers(examMembers);

    return exam;
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
  public ExamClosedAnswer getExamClosedAnswerByIdAndIdExamQuestion(int id, int idExamQuestion) {
    return examClosedAnswerRepository.findExamClosedAnswerByIdAndIdExamQuestion(id, idExamQuestion);
  }

  @Override
  public ExamAnswer getExamAnswerByIdExamMemberQuestionAnIdExamClosedAnswer(
      int id_exam_member_question, int id_exam_closed_answer) {
    return examAnswerRepository
        .findExamAnswerByIdExamMemberQuestionAndIdExamClosedAnswer(id_exam_member_question,
            id_exam_closed_answer);
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
    examMember.setBlocked(!examMember.isBlocked());
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
    for (ExamAnswerWrapper eaw : examAnswerWrapper) {
      ExamMemberQuestion examMemberQuestion = examMemberQuestionRepository
          .findExamMemberQuestionById(eaw.getId_exam_member_question());

      if (examMemberQuestion == null) {
        continue;
      }

      if (loggedAccount.getId() != examMemberQuestion.getExamMember().getAccount().getId()) {
        continue;
      }

      int currentPosition = examMemberQuestion.getPosition();
      examMemberQuestion.getExamMember().setPosition(currentPosition + 1);
      examMemberRepository.save(examMemberQuestion.getExamMember());

      List<ExamAnswer> examAnswers = examAnswerRepository
          .findExamAnswersByIdExamMemberQuestion(examMemberQuestion.getId());
      examAnswerRepository.deleteAll(examAnswers);

      List<ExamClosedAnswer> examClosedAnswers = new ArrayList<>();
      for (Integer listIdExamClosedAnswer : eaw.getId_exam_closed_answers()) {
        ExamClosedAnswer examClosedAnswer = examClosedAnswerRepository
            .findExamClosedAnswerByIdAndIdExamQuestion(listIdExamClosedAnswer,
                examMemberQuestion.getExamQuestion().getId());
        if (examClosedAnswer != null) {
          examClosedAnswers.add(examClosedAnswer);
        }
      }

      if (!examClosedAnswers.isEmpty()) {
        for (ExamClosedAnswer eca : examClosedAnswers) {
          ExamAnswer examAnswer = new ExamAnswer();
          examAnswer.setExamMemberQuestion(examMemberQuestion);
          examAnswer.setExamClosedAnswer(eca);
          examAnswerRepository.save(examAnswer);
        }
      } else {
        ExamAnswer examAnswer = new ExamAnswer();
        examAnswer.setExamMemberQuestion(examMemberQuestion);
        examAnswer.setExamClosedAnswer(null);
        examAnswerRepository.save(examAnswer);
      }
    }
  }

  @Override
  public Exam finishExam(Exam exam) {
    exam.setJoining(true);
    exam.setStarted(true);
    exam.setFinished(true);
    exam.setFinishTime(new Timestamp(new Date().getTime()));

    if (exam.isSendResultsInstantly()) {
      exam.setRated(true);
    }

    exam = examRepository.save(exam);
    return exam;
  }

  @Override
  public void changeExamMemberPosition(Exam exam, int position) {
    Account loggedAccount = accountService.getLoggedAccount();
    ExamMember examMember = examMemberRepository
        .findExamMemberByIdAccountAndIdExam(loggedAccount.getId(), exam.getId());

    if (examMember == null) {
      return;
    }

    examMember.setPosition(position);
    examMemberRepository.save(examMember);
  }

  @Override
  public void changeCorrectExamClosedAnswer(ExamClosedAnswer examClosedAnswer) {
    examClosedAnswer.setCorrect(!examClosedAnswer.isCorrect());
    examClosedAnswerRepository.save(examClosedAnswer);
  }

  @Override
  public void rateExam(int id) {
    Exam exam = examRepository.findExamById(id);
    if (exam == null) {
      return;
    }

    exam.setRated(true);
    examRepository.save(exam);
  }

}
