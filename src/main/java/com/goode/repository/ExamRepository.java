package com.goode.repository;

import com.goode.business.Exam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamRepository extends CrudRepository<Exam, Long> {

  Exam findExamById(int id);

  @Query(value = "select COALESCE(MAX(eq.position)+1, '0') as nextorder from exam_questions eq where eq.id_exam=:id_exam", nativeQuery = true)
  int getNextQuestionPosition(@Param("id_exam") int id_exam);

}
