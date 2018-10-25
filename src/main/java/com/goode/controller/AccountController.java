package com.goode.controller;

import com.goode.ErrorMessage;
import com.goode.ErrorCode;
import com.goode.Language;
import com.goode.SendEmail;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.ActivationCode;
import com.goode.service.AccountService;
import com.goode.validator.AccessRoleValidator;
import com.goode.validator.AccountValidator;
import com.goode.validator.ActivationCodeValidator;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController<Account, AccountService> {

  @Autowired
  private AccountService accountService;

  @Autowired
  private SendEmail sendEmail;

  @Autowired
  private AccountValidator accountValidator;

  @Autowired
  private ActivationCodeValidator activationCodeValidator;

  @Autowired
  private AccessRoleValidator accessRoleValidator;

  @GetMapping("/getLoggedAccount")
  public ResponseEntity<?> getLoggedUser(Principal principal){
    Account account = accountService.getAccountByPrincipal(principal);
    if(account == null) {
      account = new Account();
      account.setUsername("");
    }

    return new ResponseEntity<>(account, HttpStatus.OK);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(HttpServletRequest request,
      @Validated(Account.ValidationStepOne.class) @RequestBody Account account,
      BindingResult result) {
    super.initializeService(accountService);

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Account newAccount = super.addNew(account);
    if (newAccount == null) {
      return ErrorMessage
          .send(Language.getMessage("error.account.notCreated"), HttpStatus.BAD_REQUEST);
    }

    sendEmail.send(newAccount.getEmail(), Language.getMessage("email.registration.title"),
        Language.getMessage("hello") + " " + newAccount.getUsername() + "!\n" +
            Language.getMessage("email.activationLink") + "\n" +
            "http://" + request.getLocalName() + "/account/activate/" + newAccount
            .getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/resendActivationCode")
  public ResponseEntity<?> resendActivationCode(HttpServletRequest request,
      @RequestParam("email") String email) {

    ErrorCode errorCode = new ErrorCode();
    Account account = accountValidator.validateAccountActivation(email, errorCode);

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    account = accountService
        .generateActivationCode(account, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE);
    if (account == null) {
      return ErrorMessage.send(Language.getMessage("error.activationCode.send.tooMany"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    sendEmail.send(account.getEmail(), Language.getMessage("email.activationCode.title"),
        Language.getMessage("hello") + " " + account.getUsername() + "!\n" +
            Language.getMessage("email.activationCode.request") + " " +
            Language.getMessage("email.activationLink") + "\n" +
            "http://" + request.getLocalName() + "/account/activate/" + account.getActivationCodes()
            .get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/activate/{activationCode}")
  public ResponseEntity<?> activateAccount(@PathVariable("activationCode") String code) {
    ErrorCode errorCode = new ErrorCode();
    ActivationCode activationCode = activationCodeValidator
        .validateCode(code, ActivationCode.TYPE_ACTIVATION_ACCOUNT_CODE, errorCode);

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if (!accountService.activateAccount(activationCode)) {
      return ErrorMessage.send(Language.getMessage("error.account.notActivated"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/sendResetPasswordRequest")
  public ResponseEntity<?> sendResetPassword(HttpServletRequest request,
      @RequestParam("email") String email) {
    ErrorCode errorCode = new ErrorCode();
    Account account = accountValidator.validateEmail(email, errorCode);

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    account = accountService
        .generateActivationCode(account, ActivationCode.TYPE_RESET_PASSWORD_CODE);
    if (account == null) {
      return ErrorMessage.send(Language.getMessage("error.activationCode.send.tooMany"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    sendEmail.send(account.getEmail(), Language.getMessage("email.account.resetPassword.title"),
        Language.getMessage("hello") + " " + account.getUsername() + "!\n" +
            Language.getMessage("email.account.resetPassword.body") + "\n" +
            "http://" + request.getLocalName() + "/account/resetPassword/" + account
            .getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/resetPassword/{resetPasswordCode}")
  public ResponseEntity<?> resetPasswordRequest(
      @PathVariable("resetPasswordCode") String resetPasswordCode) {
    ErrorCode errorCode = new ErrorCode();
    ActivationCode activationCode = activationCodeValidator
        .validateCode(resetPasswordCode, ActivationCode.TYPE_RESET_PASSWORD_CODE, errorCode);
    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/resetPassword/{resetPasswordCode}")
  public ResponseEntity<?> resetPassword(
      @PathVariable("resetPasswordCode") String resetPasswordCode,
      @RequestParam("password") String password) {
    ErrorCode errorCode = new ErrorCode();
    ActivationCode activationCode = activationCodeValidator
        .validateCode(resetPasswordCode, ActivationCode.TYPE_RESET_PASSWORD_CODE, errorCode);
    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    accountValidator.validatePassword(password, errorCode);

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if (!accountService.resetPassword(activationCode, password)) {
      return ErrorMessage.send(Language.getMessage("error.account.resetPassword"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{accountId}/changeAccessRole")
  @PreAuthorize("hasRole('" + AccessRole.ROLE_ADMIN + "')")
  public ResponseEntity<?> changeAccessRole(@PathVariable("accountId") int accountId,
      @RequestParam("accessRole") String accessRole) {

    ErrorCode errorCode = new ErrorCode();
    Account account = accessRoleValidator.validateChangeAccountAccessRole(accountId, accessRole, errorCode);

    if (errorCode.hasErrors()) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if (!accountService.changeAccessRole(account, accessRole)) {
      return ErrorMessage.send(Language.getMessage("error.account.changeAccessRole.internalError"),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @GetMapping("/getAll")
  @PreAuthorize("hasRole('" + AccessRole.ROLE_ADMIN + "')")
  public ResponseEntity<?> getAll(){
    return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
  }

}
