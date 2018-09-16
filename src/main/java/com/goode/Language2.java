package com.goode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public abstract class Language2 {

  public static String getMessage(String code, String... args) {
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

  public static String translateError(String field, String code, String defaultError,
      String... args) {
    switch (code) {
      case "NotNull":
        return getMessage("validate.NotNull", getMessage(field));
      case "NotBlank":
        return getMessage("validate.NotBlank", getMessage(field));
      case "Length":
        return translateErrorLength(field, defaultError);
      case "Min":
        return translateErrorLength(field, defaultError);
      case "Max":
        return translateErrorLength(field, defaultError);
      case "Pattern":
        return translatePattern(field);
    }

    return getMessage(code, args);
  }

  private static String translateErrorLength(String field, String defaultError) {
    Pattern p = Pattern.compile("(\\d+)");
    Matcher m = p.matcher(defaultError);
    List<Integer> valueTab = new ArrayList<>();
    String translate = "";
    while (m.find()) {
      valueTab.add(Integer.parseInt(m.group(1)));
    }

    if (valueTab.size() == 2) {
      translate =
          valueTab.get(1) == 2147483647 ? getMessage("validate.Length.greaterOrEqual", getMessage(field), valueTab.get(0).toString())
              : getMessage("validate.Length.between", getMessage(field), valueTab.get(0).toString(), valueTab.get(1).toString());
    } else if (valueTab.size() == 1) {
      if (defaultError.matches("(must be greater than or equal to)(.*)")) {
        translate = getMessage("validate.Min", getMessage(field), valueTab.get(0).toString());
      } else if (defaultError.matches("(must be less than or equal to)(.*)")) {
        translate = getMessage("validate.Max", getMessage(field), valueTab.get(0).toString());
      }
    }

    return translate;
  }

  private static String translatePattern(String field){
    switch(field){
      case "username": return getMessage("validate.lettersNumbersAndUnderscoreOnly", getMessage(field));
      case "email": return getMessage("validate.email.incorrect");
      case "firstName": return getMessage("validate.lettersOnly", getMessage(field));
      case "lastName": return getMessage("validate.lettersOnly", getMessage(field));
    }

    return getMessage("error.unknown");
  }
}
