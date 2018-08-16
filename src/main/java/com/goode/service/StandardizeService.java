package com.goode.service;

import org.springframework.http.ResponseEntity;

public interface StandardizeService<E> {

  ResponseEntity<?> addNew(E entity);

}
