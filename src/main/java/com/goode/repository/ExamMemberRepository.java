package com.goode.repository;

import com.goode.business.ExamMember;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExamMemberRepository extends CrudRepository<ExamMember, Long> {

  ExamMember findExamMemberById(int id);

  @Query(value = "select * from exam_members where id_account=:id_account and id_exam=:id_exam", nativeQuery = true)
  ExamMember findExamMemberByIdAccountAndIdExam(@Param("id_account") int id_account, @Param("id_exam") int id_exam);

  @Query(value = "select * from exam_members where id_exam=:id_exam", nativeQuery = true)
  List<ExamMember> findExamMembersByIdExam(@Param("id_exam") int id_exam);

}
