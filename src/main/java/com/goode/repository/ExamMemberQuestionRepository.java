package com.goode.repository;

import com.goode.business.ExamMemberQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamMemberQuestionRepository extends CrudRepository<ExamMemberQuestion, Long> {

  ExamMemberQuestion findExamMemberQuestionById(int id);

  @Query(value = "select * from exam_member_questions where id_exam_member=:id_exam_member order by position ASC", nativeQuery = true)
  List<ExamMemberQuestion> findAllByIdExamMember(@Param("id_exam_member") int id_exam_member);
}
