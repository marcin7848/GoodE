package com.goode.service;

import com.goode.business.QuestionGroup;
import org.springframework.stereotype.Service;

@Service
public class QuestionGroupService implements QuestionGroupServiceI, StandardizeService<QuestionGroup> {

  @Override
  public QuestionGroup addNew(QuestionGroup questionGroup) {
    return new QuestionGroup();
  }

  @Override
  public QuestionGroup edit(QuestionGroup questionGroup) {
    return new QuestionGroup();
  }


  @Override
  public void delete(QuestionGroup questionGroup) {

  }
}