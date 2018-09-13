package com.goode;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public abstract class Language2 {

  public static String getMessage(String code, String ... args){
    MessageSource messageSource = messageSource();
    return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
  }

  private static MessageSource messageSource() {
    ResourceBundleMessageSource source = new ResourceBundleMessageSource();
    source.setBasename("messages");
    source.setUseCodeAsDefaultMessage(true);
    source.setDefaultEncoding("UTF-8");
    return source;
  }

  public static String translateError(String field, String code, String defaultError, String ... args){
    switch (code){
      case "NotNull": return getMessage("validate.NotNull", getMessage(field));
      case "NotBlank": return getMessage("validate.NotBlank", getMessage(field));
      case "Length": return translateErrorLength(field, defaultError);
    }

    return getMessage(code, args);
  }

  private static String translateErrorLength(String field, String defaultError){
    return "regEx";
    //pobranie regexem 2 cyfr i komentarz, że liczba musi być pomiędzy min i max, chyba, że max to: 2147483647 to wtedy, że liczba musi być min i tyle
  }
}
