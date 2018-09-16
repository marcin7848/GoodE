package com.goode.controller;

import com.goode.ErrorMessage;
import com.goode.ErrorCode;
import com.goode.Language;
import com.goode.Language2;
import com.goode.SendEmail;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.service.AccountService;
import com.goode.validator.AccountValidator;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @Autowired
  private AccountValidator accountValidator;

  @PostMapping("/register")
  public ResponseEntity<?> register(HttpServletRequest request,
      @Validated(Account.ValidationStepOne.class) @RequestBody Account account,
      BindingResult result) {
    super.initializeService(accountService);

    accountValidator.validateAccount(account, result);

    if (result.hasErrors()) {
      return ErrorMessage.send(Language2
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language2.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Account newAccount = super.addNew(account);
    if (newAccount == null) {
      return ErrorMessage.send(Language2.getMessage("error.account.notCreated"), HttpStatus.BAD_REQUEST);
    }

    sendEmail.send(newAccount.getEmail(), Language2.getMessage("email.registration.title"),
        Language2.getMessage("hello") + " " + newAccount.getUsername() + "!\n" +
            Language2.getMessage("email.activationLink") + "\n" +
            "http://" + request.getLocalName() + "/account/activate/" + newAccount
            .getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/resendActivationCode")
  public ResponseEntity<?> resendActivationCode(HttpServletRequest request,
      @RequestParam("email") String email) {

    ErrorCode errorCode = new ErrorCode();
    Account account = accountValidator.validateAccountActivation(email, errorCode) ;

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language2.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    account = accountService
        .generateActivationCode(account, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);
    if (account == null) {
      return ErrorMessage.send(Language2.getMessage("error.activationCode.send.tooMany"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    sendEmail.send(account.getEmail(), Language2.getMessage("email.activationCode.title"),
        Language2.getMessage("hello") + " " + account.getUsername() + "!\n" +
            Language2.getMessage("email.activationCode.request") + " " +
            Language2.getMessage("email.activationLink") + "\n" +
            "http://" + request.getLocalName() + "/account/activate/" + account.getActivationCodes()
            .get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/activate/{activationCode}")
  public ResponseEntity<?> activateAccount(@PathVariable("activationCode") String activationCode) {
    if (!accountService.activateAccount(activationCode)) {
      return accountService
          .sendError(Language.ACCOUNT_NOT_ACTIVATED.getString(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/sendResetPasswordRequest")
  public ResponseEntity<?> sendResetPassword(HttpServletRequest request,
      @RequestParam("email") String email) {
    Account account = accountService.sendResetPasswordValidation(email);
    if (account == null) {
      return accountService.sendError(Language.EMAIL_INCORRECT.getString(), HttpStatus.BAD_REQUEST);
    }

    account = accountService
        .generateActivationCode(account, ActivationCode.TYPE_RESET_PASSWORD_CODE);
    if (account == null) {
      return accountService.sendError(Language.RESEND_ACTIVATION_CODE_TO_MANY.getString(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    sendEmail.send(account.getEmail(), Language.EMAIL_RESET_PASSWORD_TITLE.getString(),
        Language.HELLO.getString() + " " + account.getUsername() + "!\n" +
            Language.EMAIL_RESET_PASSWORD.getString() +
            "http://" + request.getLocalName() + "/account/resetPassword/" + account
            .getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/resetPassword/{resetPasswordCode}")
  public ResponseEntity<?> resetPasswordRequest(
      @PathVariable("resetPasswordCode") String resetPasswordCode) {
    if (accountService.resetPasswordRequest(resetPasswordCode) == null) {
      return accountService
          .sendError(Language.ACTIVATION_CODE_INCORRECT.getString(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/resetPassword/{resetPasswordCode}")
  public ResponseEntity<?> resetPassword(@PathVariable("resetPasswordCode") String activationCode,
      @RequestParam("password") String password) {
    if (!accountService.resetPassword(activationCode, password)) {
      return accountService
          .sendError(Language.RESET_PASSWORD_ERROR.getString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{accountId}/changeAccessRole")
  @PreAuthorize("hasRole('" + AccessRole.ROLE_ADMIN + "')")
  public ResponseEntity<?> changeAccessRole(@PathVariable("accountId") int accountId,
      @RequestParam("accessRole") String accessRole) {

    if (!accountService.changeAccessRole(accountId, accessRole)) {
      return accountService.sendError(Language.CHANGE_ACCESS_ROLE_ERROR.getString(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

}
