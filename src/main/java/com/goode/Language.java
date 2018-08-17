package com.goode;

public enum Language {

  REGISTRATION_GOODE("Rejestracja GoodE", "Registration GoodE"),
  USERNAME_LENGTH_BETWEEN("Długość nazwy użytkownika musi być pomiędzy", "Username's length has to be between");

  private String polish;
  private String english;

  Language(String polish, String english){
    this.polish = polish;
    this.english = english;
  }

  public String getString(){
    boolean polish_active = true;

    if(polish_active)
      return this.polish;
    return this.english;
  }
}
