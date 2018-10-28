package com.goode.controller;

import com.goode.ErrorMessage;
import com.goode.ErrorCode;
import com.goode.Language;
import com.goode.SendEmail;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Account.RegisterValidation;
import com.goode.business.ActivationCode;
import com.goode.service.AccountService;
import com.goode.validator.AccessRoleValidator;
import com.goode.validator.AccountValidator;
import com.goode.validator.ActivationCodeValidator;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController<Account, AccountService> {

  @Value("${angular.host}")
  private String angularHost;

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
      @Validated(RegisterValidation.class) @RequestBody Account account,
      BindingResult result) {
    super.initializeService(accountService);

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    if (!accountService.getAccountByUsernameOrEmail(account.getUsername(), account.getEmail())
        .isEmpty()) {
      return ErrorMessage
          .send(Language.getMessage("error.account.alreadyExists"), HttpStatus.BAD_REQUEST);
    }

    Account newAccount = super.addNew(account);
    if (newAccount == null) {
      return ErrorMessage
          .send(Language.getMessage("error.account.notCreated"), HttpStatus.BAD_REQUEST);
    }

    sendEmail.send(newAccount.getEmail(), Language.getMessage("email.registration.title"),
        Language.getMessage("hello") + " " + newAccount.getUsername() + "!\n" +
            Language.getMessage("email.activationLink") + "\n"
            + angularHost + "/account/activate/" + newAccount
            .getActivationCodes().get(0).getCode());

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/resendActivationCode")
  public ResponseEntity<?> resendActivationCode(HttpServletRequest request,
      @RequestBody Map<String, Object> emailObj) {

    String email = (String)emailObj.get("email");

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
            angularHost + "/account/activate/" + account.getActivationCodes()
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
      @RequestBody Map<String, Object> emailObj) {

    String email = (String)emailObj.get("email");

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
            angularHost + "/account/resetPassword/" + account
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
      @RequestBody Map<String, Object> passwordObj) {

    String password = (String)passwordObj.get("password");

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

  @PostMapping("/{username}/changeAccessRole")
  @PreAuthorize("hasRole('" + AccessRole.ROLE_ADMIN + "')")
  public ResponseEntity<?> changeAccessRole(@PathVariable("username") String username,
      @RequestBody Map<String, Object> accessRoleObj) {

    String accessRole = (String) accessRoleObj.get("accessRole");

    Account account = accountService.getAccountByUsername(username);

    if(account == null){
      return ErrorMessage.send(Language.getMessage("error.account.badUsername"), HttpStatus.BAD_REQUEST);
    }

    int accountId = account.getId();

    ErrorCode errorCode = new ErrorCode();
    account = accessRoleValidator.validateChangeAccountAccessRole(accountId, accessRole, errorCode);

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

  @GetMapping("/getAllAccessRole")
  @PreAuthorize("hasRole('" + AccessRole.ROLE_ADMIN + "')")
  public ResponseEntity<?> getAllAccessRole(){
    return new ResponseEntity<>(accountService.getAllAccessRole(), HttpStatus.OK);
  }


}
