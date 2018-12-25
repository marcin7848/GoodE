package com.goode.controller;

import com.goode.ErrorCode;
import com.goode.ErrorMessage;
import com.goode.Language;
import com.goode.business.AccessRole;
import com.goode.business.Account;
import com.goode.business.Exam;
import com.goode.business.Exam.ExamValidationFull;
import com.goode.business.Exam.StartExamValidationFull;
import com.goode.business.ExamAnswer;
import com.goode.business.ExamAnswerWrapper;
import com.goode.business.ExamClosedAnswer;
import com.goode.business.ExamMember;
import com.goode.business.ExamMemberQuestion;
import com.goode.business.ExamQuestion;
import com.goode.business.Group;
import com.goode.business.Question;
import com.goode.service.AccountService;
import com.goode.service.ExamService;
import com.goode.service.GroupService;
import com.goode.service.QuestionService;
import com.goode.validator.ExamValidator;
import com.goode.validator.GroupMemberValidator;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exam")
public class ExamController {

  @Autowired
  ExamService examService;

  @Autowired
  GroupService groupService;

  @Autowired
  QuestionService questionService;

  @Autowired
  AccountService accountService;

  @Autowired
  ExamValidator examValidator;

  @Autowired
  GroupMemberValidator groupMemberValidator;

  @GetMapping("/getAll/group/{idGroup}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> getAllExamsByGroup(@PathVariable("idGroup") int idGroup){

    Group group = groupService.getGroupById(idGroup);
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateStudentInGroup(group, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(examService.getAllExamByIdGroup(group), HttpStatus.OK);
  }

  @GetMapping("/{id}/get")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> getExamFull(@PathVariable("id") int id){

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(examService.getExamFullById(id), HttpStatus.OK);
  }

  @GetMapping("/{id}/getResults")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> getResults(@PathVariable("id") int id){

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateStudentInGroup(group, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    Account loggedAccount = accountService.getLoggedAccount();

    ExamMember examMember = examService.getExamMemberByIdAccountAndIdExam(loggedAccount.getId(), exam.getId());
    if(examMember == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.examMember.notExisted"), HttpStatus.BAD_REQUEST);
    }

    int[] results = examService.getResultsExam(exam);
    JSONObject obj = new JSONObject();
    obj.put("maxPoints", results[0]);
    obj.put("points", results[1]);

    return new ResponseEntity<>(obj.toMap(), HttpStatus.OK);
  }

  @GetMapping("/{id}/get/runningManagement")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> getRunningExamManagement(@PathVariable("id") int id){

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(examService.getRunningExamManagement(id), HttpStatus.OK);
  }

  @GetMapping("/{id}/get/running")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"' )")
  public ResponseEntity<?> getRunningExam(@PathVariable("id") int id){

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validateStudentInGroup(group, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(examService.getRunningExam(id), HttpStatus.OK);
  }

  @PostMapping("/addNew/group/{idGroup}")
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
      @PathVariable("id") int id){

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

    Group group = groupService.getGroupById(examById.getGroup().getId());
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

    if(examById.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.editedNotPossible.examStarted"), HttpStatus.INTERNAL_SERVER_ERROR);
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

  @PostMapping("/{idExam}/examQuestion/addAll")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> addAllExamQuestion(@PathVariable("idExam") int idExam){

    Exam exam = examService.getExamById(idExam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(exam.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.editedNotPossible.examStarted"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    examService.addAllExamQuestion(exam, group);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/examQuestion/add/{idQuestion}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> addNewExamQuestion(
      @PathVariable("idQuestion") int idQuestion,
      @PathVariable("idExam") int idExam){

    Exam exam = examService.getExamById(idExam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    Question question = questionService.getQuestionById(idQuestion);
    if(question == null){
      return ErrorMessage
          .send(Language.getMessage("error.question.badId"), HttpStatus.BAD_REQUEST);
    }

    if(exam.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.editedNotPossible.examStarted"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ExamQuestion examQuestion = examService.addNewExamQuestion(exam, question);
    if(examQuestion == null){
      return ErrorMessage
          .send(Language.getMessage("error.examQuestion.notAdded"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @DeleteMapping("/{idExam}/examQuestion/{id}/delete")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> deleteExamQuestion(
      @PathVariable("id") int id,
      @PathVariable("idExam") int idExam){

    Exam exam = examService.getExamById(idExam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(exam.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.editedNotPossible.examStarted"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ExamQuestion examQuestion = examService.getExamQuestionById(id);
    if(examQuestion == null){
      return ErrorMessage
          .send(Language.getMessage("error.examQuestion.badId"), HttpStatus.BAD_REQUEST);
    }


    examService.deleteExamQuestion(examQuestion);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @PatchMapping("/{idExam}/examQuestion/{id}/changePosition/{position}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> changeExamQuestionPosition(
      @PathVariable("id") int id,
      @PathVariable("idExam") int idExam,
      @PathVariable("position") int position){

    Exam exam = examService.getExamById(idExam);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    ExamQuestion examQuestion = examService.getExamQuestionById(id);
    if(examQuestion == null){
      return ErrorMessage
          .send(Language.getMessage("error.examQuestion.badId"), HttpStatus.BAD_REQUEST);
    }

    if(exam.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.editedNotPossible.examStarted"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if(!examService.changePositionExamQuestion(examQuestion, position)){
      return ErrorMessage
          .send(Language.getMessage("error.examQuestion.positionNotChanged"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/initiateExam")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> initiateExam(
      @Validated(StartExamValidationFull.class) @RequestBody Exam exam,
      BindingResult result,
      @PathVariable("idExam") int idExam){

    if (result.hasErrors()) {
      System.out.println(result.getFieldError().getCode());
      System.out.println(result.getFieldError().getDefaultMessage());
      return ErrorMessage.send(Language
          .translateError(result.getFieldError().getField(), result.getFieldError().getCode(),
              result.getFieldError().getDefaultMessage(),
              Language.getMessage(result.getFieldError().getField())), HttpStatus.BAD_REQUEST);
    }

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(examById.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(examById.isJoining()){
      return new ResponseEntity<>(null, HttpStatus.OK);
    }

    examById.setPassword(exam.getPassword());
    examById.setColor(exam.getColor());

    if(!examValidator.validateInitiateJoiningExam(examById, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    exam = examService.initiateJoining(examById);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.initiateJoinNotCorrect"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/join")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> joinToExam(@PathVariable("idExam") int idExam,
      @RequestBody Map<String, Object> passwordObj){
    String password = (String) passwordObj.get("password");

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(examById.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if(!groupMemberValidator.validateStudentInGroup(group, errorCode)){
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(!examById.isJoining()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.join.notInitiatedJoin"), HttpStatus.BAD_REQUEST);
    }

    if(examById.isStarted()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.join.examAlreadyStarted"), HttpStatus.BAD_REQUEST);
    }

    if(!examById.getPassword().equals(password)){
      return ErrorMessage
          .send(Language.getMessage("error.exam.join.wrongPassword"), HttpStatus.BAD_REQUEST);
    }

    Account loggedAccount = accountService.getLoggedAccount();
    if(examService.getExamMemberByIdAccountAndIdExam(loggedAccount.getId(), examById.getId()) != null){
      return new ResponseEntity<>(null, HttpStatus.OK);
    }

    ExamMember examMember = examService.joinToExam(examById);
    if(examMember == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.join.notJoined"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/examMember/{idExamMember}/block")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> blockExamMember(@PathVariable("idExam") int idExam,
      @PathVariable("idExamMember") int idExamMember,
      @RequestBody Map<String, Object> causeOfBlockadeObj){

    String causeOfBlockade = (String) causeOfBlockadeObj.get("causeOfBlockade");

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(examById.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    ExamMember examMember = examService.getExamMemberById(idExamMember);
    if(examMember == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.examMember.notExisted"), HttpStatus.BAD_REQUEST);
    }

    examService.blockExamMember(examMember, causeOfBlockade);
    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/start")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> startExam(
      @PathVariable("idExam") int idExam,
      @RequestBody Map<String, Object> finishTimeObj) throws ParseException {

    String timestampString = (String) finishTimeObj.get("finishTime");
    Timestamp timestamp = new Timestamp(new Date().getTime());

    if(!timestampString.equals("")){
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
      Date parsedDate = dateFormat.parse(timestampString);
      timestamp = new java.sql.Timestamp(parsedDate.getTime());
    }

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(examById.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(examById.isStarted()){
      return new ResponseEntity<>(null, HttpStatus.OK);
    }

    if(timestamp.getTime() < new Timestamp(new Date().getTime()).getTime() && (examById.getType() == 1 || examById.getType() == 2)){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badFinishTimestamp"), HttpStatus.BAD_REQUEST);
    }

    examById = examService.startExam(examById, timestamp);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.notStarted"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PostMapping("/{idExam}/addAnswer")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"')")
  public ResponseEntity<?> addExamAnswer(
      @PathVariable("idExam") int idExam,
      @RequestBody List<ExamAnswerWrapper> examAnswerWrapper) throws ParseException {

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    if(examById.isFinished()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.isFinished"), HttpStatus.BAD_REQUEST);
    }

    examService.addExamAnswer(examById, examAnswerWrapper);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @PostMapping("/{idExam}/finish")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> finishExam(
      @PathVariable("idExam") int idExam) throws ParseException {

    Exam examById = examService.getExamById(idExam);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(examById.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    if(examById.isFinished()){
      return ErrorMessage
          .send(Language.getMessage("error.exam.isFinished"), HttpStatus.BAD_REQUEST);
    }

    examById = examService.finishExam(examById);
    if(examById == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.notFinished"), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(null, HttpStatus.OK);
  }


  @PostMapping("/{id}/changeExamMemberPosition/{position}")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"', '"+ AccessRole.ROLE_STUDENT +"' )")
  public ResponseEntity<?> changeExamMemberPosition(@PathVariable("id") int id,
      @PathVariable("position") int position){

    if(position < 0){
      position = 0;
    }

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validateStudentInGroup(group, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    examService.changeExamMemberPosition(exam, position);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }

  @PatchMapping("/{id}/examQuestion/{idExamQuestion}/examClosedAnswer/{idExamClosedAnswer}/changeCorrect")
  @PreAuthorize("hasAnyRole('"+ AccessRole.ROLE_ADMIN +"', '"+ AccessRole.ROLE_TEACHER +"')")
  public ResponseEntity<?> changeCorrectExamClosedAnswer(
      @PathVariable("id") int id,
      @PathVariable("idExamQuestion") int idExamQuestion,
      @PathVariable("idExamClosedAnswer") int idExamClosedAnswer){

    Exam exam = examService.getExamById(id);
    if(exam == null){
      return ErrorMessage
          .send(Language.getMessage("error.exam.badId"), HttpStatus.BAD_REQUEST);
    }

    Group group = groupService.getGroupById(exam.getGroup().getId());
    if(group == null){
      return ErrorMessage
          .send(Language.getMessage("error.group.badId"), HttpStatus.BAD_REQUEST);
    }

    ErrorCode errorCode = new ErrorCode();
    if (!groupMemberValidator.validatePermissionToGroup(group, true, errorCode)) {
      return ErrorMessage.send(Language.getMessage(errorCode.getCode()), HttpStatus.BAD_REQUEST);
    }

    ExamQuestion examQuestion = examService.getExamQuestionById(idExamQuestion);
    if(examQuestion == null){
      return ErrorMessage
          .send(Language.getMessage("error.examQuestion.badId"), HttpStatus.BAD_REQUEST);
    }

    ExamClosedAnswer examClosedAnswer = examService.getExamClosedAnswerByIdAndIdExamQuestion(idExamClosedAnswer, idExamQuestion);
    if(examClosedAnswer == null){
      return ErrorMessage
          .send(Language.getMessage("error.examClosedAnswer.badId"), HttpStatus.BAD_REQUEST);
    }

    examService.changeCorrectExamClosedAnswer(examClosedAnswer);

    return new ResponseEntity<>(null, HttpStatus.OK);
  }
}
