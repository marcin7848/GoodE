package com.goode.repository;

import com.goode.business.ExamClosedAnswer;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamClosedAnswerRepository extends CrudRepository<ExamClosedAnswer, Long> {

  ExamClosedAnswer findExamClosedAnswerById(int id);

  @Query(value = "select * from exam_closed_answers where id_exam_question=:id_exam_question", nativeQuery = true)
  List<ExamClosedAnswer> findAllExamClosedAnswerByIdExamQuestion(@Param("id_exam_question") int id_exam_question);

  @Query(value = "select * from exam_closed_answers where id_exam_closed_answer=:id and id_exam_question=:id_exam_question", nativeQuery = true)
  ExamClosedAnswer findExamClosedAnswerByIdAndIdExamQuestion(@Param("id") int id, @Param("id_exam_question") int id_exam_question);

}
