package com.goode;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class ErrorMessage {

  public static ResponseEntity<?> send(String error, HttpStatus httpStatus){
    JSONObject obj = new JSONObject();
    obj.put("error", error);
    return new ResponseEntity<>(obj.toMap(), httpStatus);
  }
}
