package com.goode.service;

import com.goode.ErrorCode;

public interface StandardizeService<E> {

  E addNew(E entity);
  E edit(E entity);
  void delete(E entity);

}
