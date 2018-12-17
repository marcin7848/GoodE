package com.goode.controller;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Exam;
import com.goode.business.Exam.ExamValidationFull;
import com.goode.business.Group;
import com.goode.service.ExamService;
import com.goode.service.GroupService;
import com.goode.validator.ExamValidator;
import com.goode.validator.GroupMemberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/group/{idGroup}/exam")
public class ExamController {

  @Autowired
  ExamService examService;

  @Autowired
  GroupService groupService;

  @Autowired
  ExamValidator examValidator;

  @Autowired
  GroupMemberValidator groupMemberValidator;

  @PostMapping("/addNew")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> addNew(@Validated(ExamValidationFull.class) @RequestBody Exam exam,
      BindingResult result,
      @PathVariable("idGroup") int idGroup){

    if (result.hasErrors()) {
      System.out.println(result.getFieldError().getCode());
      System.out.println(result.getFieldError().getDefaultMessage());
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

    errorCode = new ErrorCode();

    if (!examValidator.validateExamData(exam, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    exam.setGroup(group);

    exam = examService.addNew(exam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.notCreated"), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/edit")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> editExam(@Validated(ExamValidationFull.class) @RequestBody Exam exam,
      BindingResult result,
      @PathVariable("id") int id,
      @PathVariable("idGroup") int idGroup){

    if (result.hasErrors()) {
      System.out.println(result.getFieldError().getCode());
      System.out.println(result.getFieldError().getDefaultMessage());
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Exam examById = examService.getExamById(id);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
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

    errorCode = new ErrorCode();

    if (!examValidator.validateExamData(exam, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    exam.setGroup(group);
    exam.setId(id);

    exam = examService.edit(exam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.notEdited"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


}
