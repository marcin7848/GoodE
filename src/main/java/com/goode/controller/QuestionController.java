package com.goode.controller;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.business.Question.FullValidation;
import com.goode.service.AccountService;
import com.goode.service.GroupService;
import com.goode.service.QuestionService;
import com.goode.validator.GroupMemberValidator;
import com.goode.validator.GroupValidator;
import com.goode.validator.QuestionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group/{idGroup}/question")
public class QuestionController {

  @Autowired
  QuestionService questionService;

  @Autowired
  AccountService accountService;

  @Autowired
  GroupService groupService;

  @Autowired
  QuestionValidator questionValidator;

  @Autowired
  GroupMemberValidator groupMemberValidator;

  @PostMapping("/addNew")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> addNew(@Validated(FullValidation.class) @RequestBody Question question,
      @PathVariable("idGroup") int idGroup,
      BindingResult result){

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(idGroup);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    question = questionService.addNew(question, group);
    if(question == null){
      return ErrorMessage
          .send(Language.getMessage("error.question.notCreated"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/edit")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> edit(@Validated(FullValidation.class) @RequestBody Question question,
      @PathVariable("id") int id,
      @PathVariable("idGroup") int idGroup,
      BindingResult result){

    if (result.hasErrors()) {
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }


    Question editedQuestion = questionService.getQuestionById(id);
    if(editedQuestion == null){
      return ErrorMessage
          .send(Language.getMessage("error.question.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(idGroup);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    question.setId(editedQuestion.getId());
    question = questionService.edit(question, group);
    if(question == null){
      return ErrorMessage
          .send(Language.getMessage("error.question.notEdited"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @DeleteMapping("/{id}/delete")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> delete(@PathVariable("id") int id,
      @PathVariable("idGroup") int idGroup){

    Question question = questionService.getQuestionById(id);
    if(question == null){
      return ErrorMessage
          .send(Language.getMessage("error.question.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(idGroup);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    questionService.delete(question, group);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


}
