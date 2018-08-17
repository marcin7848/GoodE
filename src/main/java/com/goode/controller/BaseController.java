package com.goode.controller;

import com.goode.service.StandardizeService;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class BaseController<E, S extends StandardizeService<E>> {

  protected S service;

  public BaseController initializeService(S service){
    this.service = service;
    return this;
  }

  public ResponseEntity<?> addNew(E entity){
    return service.addNew(entity);
  }



}
