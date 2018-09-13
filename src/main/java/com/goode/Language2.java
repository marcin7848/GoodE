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
    return source;
  }
}
