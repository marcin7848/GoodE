package com.goode.repository;

import com.goode.business.Question;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends CrudRepository<Question, Long> {

  Question findQuestionById(int id);

  @Query(value = "select * from questions q, question_groups qg where q.id_question=qg.id_question and qg.id_group = :id_group order by q.id_question ASC", nativeQuery = true)
  List<Question> findQuestionsByIdGroup(@Param("id_group") int id_group);
}
