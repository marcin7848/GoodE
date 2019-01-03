package com.goode.repository;

import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.business.QuestionGroup;
import org.springframework.data.repository.CrudRepository;

public interface QuestionGroupRepository extends CrudRepository<QuestionGroup, Long> {

  QuestionGroup findQuestionGroupByQuestionAndGroup(Question question, Group group);

}
