package com.goode.repository;

import com.goode.business.Exam;
import org.springframework.data.repository.CrudRepository;

public interface ExamRepository extends CrudRepository<Exam, Long> {

  Exam findExamById(int id);

}
