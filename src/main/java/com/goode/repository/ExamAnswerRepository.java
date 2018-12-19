package com.goode.repository;

import com.goode.business.ExamAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamAnswerRepository extends CrudRepository<ExamAnswer, Long> {

  @Query(value = "select * from exam_answers where id_exam_member_question=:id_exam_member_question and id_exam_closed_answer=:id_exam_closed_answer", nativeQuery = true)
  ExamAnswer findExamAnswerByIdExamMemberQuestionAndIdExamClosedAnswer(@Param("id_exam_member_question") int id_exam_member_question, @Param("id_exam_closed_answer") int id_exam_closed_answer);

  @Query(value = "select * from exam_answers where id_exam_member_question=:id_exam_member_question", nativeQuery = true)
  List<ExamAnswer> findExamAnswersByIdExamMemberQuestion(@Param("id_exam_member_question") int id_exam_member_question);
}
