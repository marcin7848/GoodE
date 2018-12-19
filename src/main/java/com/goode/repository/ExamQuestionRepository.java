package com.goode.repository;

import com.goode.business.ExamQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamQuestionRepository extends CrudRepository<ExamQuestion, Long> {

  ExamQuestion findExamQuestionById(int id);

  @Query(value = "select * from exam_questions where id_exam=:id_exam ORDER BY position asc", nativeQuery = true)
  List<ExamQuestion> findAllExamQuestionsByIdExam(@Param("id_exam")int id_exam);

  @Query(value = "select COALESCE(SUM(eq.answer_time), '0') as sumTime  from exam_questions eq, exam_member_questions emq where emq.id_exam_member=:id_exam_member and eq.id_exam_question = emq.id_exam_question", nativeQuery = true)
  int getSummaryTimeForExamByIdExamMember(@Param("id_exam_member")int id_exam_member);

}
