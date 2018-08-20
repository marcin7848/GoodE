package com.goode.controller;

import com.goode.Language;
import com.goode.SendEmail;
import com.goode.business.Account;
import com.goode.service.AccountService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController extends BaseController<Account, AccountService> {

  @Autowired
  private AccountService accountService;

  @Autowired
  private SendEmail sendEmail;

  @PostMapping("/register")
  //@PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<?> register(HttpServletRequest request, @RequestBody Account account){
    super.initializeService(accountService);
    Account newAccount = super.addNew(account);
    if(newAccount == null)
      return accountService.sendError(Language.ACCOUNT_NOT_CREATED.getString(), HttpStatus.BAD_REQUEST);

    sendEmail.send(newAccount.getEmail(), Language.REGISTRATION_GOODE.getString(),
        Language.HELLO.getString() + " " + newAccount.getUsername() + "!\n" +
            Language.EMAIL_ACTIVATION_CODE.getString() +
            "http://" + request.getLocalName() + "/account/activate/" + newAccount.getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/activate/resendActivationCode")
  public ResponseEntity<?> resendActivationCode(HttpServletRequest request, @RequestParam("email") String email){
    Account account = accountService.resendActivationCode(email);
    if(account == null)
      return accountService.sendError(Language.INCORRECT_EMAIL.getString(), HttpStatus.BAD_REQUEST);

    sendEmail.send(account.getEmail(), Language.EMAIL_RESEND_ACTIVATION_CODE_TITLE.getString(),
        Language.HELLO.getString() + " " + account.getUsername() + "!\n" +
            Language.EMAIL_RESEND_ACTIVATION_CODE.getString() + " " +
            Language.EMAIL_ACTIVATION_CODE.getString() +
            "http://" + request.getLocalName() + "/account/activate/" + account.getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }
}
