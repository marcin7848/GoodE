package com.goode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCode {

  private String field;
  private String code;
  private String defaultValue;

  public boolean hasErrors(){
    return code!=null;
  }

  public void rejectValue(String field, String code){
    this.field = field;
    this.code = code;
  }

  public void rejectValue(String field, String code, String defaultValue){
    this.field = field;
    this.code = code;
    this.defaultValue = defaultValue;
  }

}
