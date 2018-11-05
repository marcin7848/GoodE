package com.goode.controller;

import com.goode.service.StandardizeService;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseController<E, S extends StandardizeService<E>> {

  protected S service;

  public BaseController initializeService(S service){
    this.service = service;
    return this;
  }

  public E addNew(E entity){
    return service.addNew(entity);
  }

  public E edit(E entity){
    return service.edit(entity);
  }

}
