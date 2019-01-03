package com.goode.repository;

import com.goode.business.ClosedAnswer;
import org.springframework.data.repository.CrudRepository;

public interface ClosedAnswerRepository extends CrudRepository<ClosedAnswer, Long> {

  ClosedAnswer findClosedAnswerById(int id);
}
