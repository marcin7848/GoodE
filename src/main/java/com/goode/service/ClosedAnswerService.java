package com.goode.service;

import com.goode.business.ClosedAnswer;
import org.springframework.stereotype.Service;

@Service
public class ClosedAnswerService implements ClosedAnswerServiceI, StandardizeService<ClosedAnswer> {

  @Override
  public ClosedAnswer addNew(ClosedAnswer question) {
    return new ClosedAnswer();
  }

  @Override
  public ClosedAnswer edit(ClosedAnswer question) {
    return new ClosedAnswer();
  }

  @Override
  public void delete(ClosedAnswer question) {

  }
}