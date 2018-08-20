package com.goode;

public enum Language {

  REGISTRATION_GOODE("Rejestracja GoodE", "Registration GoodE"),
  USERNAME_LENGTH_BETWEEN("Długość nazwy użytkownika musi być pomiędzy", "Username's length has to be between"),
  USERNAME_LETTERS_NUMBERS_UNDERSCORE_ONLY("Nazwa użytkownika może zawierać tylko litery, cyfry i znak pokreślenia.",
      "Username can contains letters, number and underscore only."),
  EMAIL_INCORRECT("Niepoprawny adres email.", "Incorrect email address."),
  FILL_ALL_FIELDS("Uzupełnij wszystkie pola.", "Fill all fields."),
  PASSWORD_LENGTH("Hasło musi mieć co najmniej 8 znaków.", "Password has to have 8 characters at least."),
  REGISTER_NO_INCORRECT("Niepoprawny numer albumu.", "Incorrect register number."),
  FIRSTNAME_LENGTH_BETWEEN("Długość imienia musi być pomiędzy", "First name's length has to be between"),
  LASTNAME_LENGTH_BETWEEN("Długość nazwiska musi być pomiędzy", "Last name's length has to be between"),
  FIRSTNAME_LETTERS_ONLY("Imię może zawierać tylko litery.", "First name can contains letters only."),
  LASTNAME_LETTERS_ONLY("Nazwisko może zawierać tylko litery.", "Last name can contains letters only."),
  ACCOUNT_EXISTS("Konto z podanymi danymi już istnieje.", "Account with given data exists."),
  UNKNOWN_ERROR("Nieznany błąd. Spróbuj ponownie później.", "Unknown error. Try again later."),
  HELLO("Witaj", "Hello"),
  EMAIL_ACTIVATION_CODE("By aktywować swoje konto wejdź w poniższy link:\n", "To activate your account click at the link below:\n"),
  ACCOUNT_NOT_CREATED("Konto nie zostało utworzone. Podane dane są niepoprawne, konto z podanymi danymi już istnieje albo wystąpił wewnętrzny błąd serwera.",
      "Account has not created. The given data are incorrect, account with given data has already existed or internal server error has occurred."),
  INCORRECT_EMAIL("Podany email nie istnieje lub konto zostało już aktywowane.", "The given email is incorrect or the account has already activated."),
  EMAIL_RESEND_ACTIVATION_CODE_TITLE("Kod aktywacyjny konta", "Account's activation code."),
  EMAIL_RESEND_ACTIVATION_CODE("Właśnie wysłałeś ponownie kod aktywacyjny Twojego konta.", "You have just resent your account's activation code.");



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
