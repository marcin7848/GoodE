package com.goode.service;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface StandardizeService<E> {

  default ResponseEntity<?> sendError(String error, HttpStatus httpStatus){
    JSONObject obj = new JSONObject();
    obj.put("error", error);
    return new ResponseEntity<>(obj.toMap(), httpStatus);
  }

  ResponseEntity<?> addNew(E entity);

}
