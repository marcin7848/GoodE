package com.goode;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class SendEmail {

  @Value("${email.username}")
  private String username;

  @Value("${email.password}")
  private String password;

  @Value("${email.fullAddress}")
  private String fullAddress;

  @Value("${email.host}")
  private String host;

  @Value("${email.port}")
  private String port;

  public boolean send(String toEmail, String subject, String message) {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);

    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

    try {
      Message createMessage = new MimeMessage(session);
      createMessage.setFrom(new InternetAddress(fullAddress));
      createMessage.setRecipients(Message.RecipientType.TO,
          InternetAddress.parse(toEmail));
      createMessage.setSubject(subject);
      createMessage.setText(message);
      Transport.send(createMessage);

      return true;

    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}