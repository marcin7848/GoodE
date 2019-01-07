import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {first} from "rxjs/operators";
import {ExamAnswerWrapper} from "../../../model/ExamAnswerWrapper";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

declare var jquery: any;
declare var $: any;

@Component({
  selector: 'app-running-exam',
  templateUrl: './running-exam.component.html',
  styleUrls: ['./running-exam.component.scss']
})
export class RunningExamComponent implements OnInit {

  idExam: number;
  loggedAccount: Account;
  exam: Exam;
  message = "";
  runningProcess = -1;
  joinToExamForm: FormGroup;
  blockade = 0;
  causeOfBlockade = "";
  answerTemp = 0;
  loading = false;

  done = 0;
  examAnswerWrapperList = [];

  timeToEndExam = {
    hours: 0,
    minutes: 0,
    seconds: 0
  };

  timeToEndQuestion = {
    hours: 0,
    minutes: 0,
    seconds: 0
  };

  examRef: Exam;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    this.joinToExamForm = this.formBuilder.group({
      joinToExamPassword: ['', Validators.required]
    });

    this.route.params.subscribe(params => {
      this.idExam = params['idExam'];


      this.examService.getRunningExam(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;

          if (this.exam.joining == false) {
            this.router.navigate(['/']);
          }
          else {
            if (this.exam.examMembers[0].id == 0) {
              this.runningProcess = 0;
              setTimeout(() => {
                this.ngOnInit();
              }, 5000);
            }
            else {
              if (this.exam.examMembers[0].blocked) {
                this.blockade = 1;
                this.causeOfBlockade = this.exam.examMembers[0].causeOfBlockade;
                this.refresh();
              } else {
                this.blockade = 0;
                if (!this.exam.finished) {
                  this.runningProcess = 1;
                  if (!this.exam.started) {
                    setTimeout(() => {
                      this.ngOnInit();
                    }, 5000);
                  }

                  setTimeout(()=> {
                    $("#idExamMemberBackground").css('background-color', this.exam.color);
                  }, 500);

                  this.refreshTimeToEnd();

                  this.refreshTimeToEndQuestion();

                  this.checkBlockade();

                  if (this.exam.showAllQuestions) {
                    for (var i = 0; i < this.exam.examMembers[0].examMemberQuestions.length; i++) {
                      var examMemberQuestion = this.exam.examMembers[0].examMemberQuestions[i];

                      for (var j = 0; j < examMemberQuestion.examQuestion.examClosedAnswers.length; j++) {
                        var answer = examMemberQuestion.examQuestion.examClosedAnswers[j];

                        for (var k = 0; k < examMemberQuestion.examAnswers.length; k++) {
                          var examAnswer = examMemberQuestion.examAnswers[k];
                          if (examAnswer.examClosedAnswer.id == answer.id) {
                            if (examMemberQuestion.examQuestion.type == 1) {
                              this.checkRadioExamAnswer(examMemberQuestion.id, answer.id);
                            }
                            if (examMemberQuestion.examQuestion.type == 2) {
                              this.checkBoxExamAnswer(examMemberQuestion.id, answer.id);
                            }
                          }
                        }
                      }
                    }
                  }
                  else {
                    for (var i = 0; i < this.exam.examMembers[0].examMemberQuestions.length; i++) {
                      var examMemberQuestion = this.exam.examMembers[0].examMemberQuestions[i];

                      if (examMemberQuestion.position == this.exam.examMembers[0].position) {

                        for (var j = 0; j < examMemberQuestion.examQuestion.examClosedAnswers.length; j++) {
                          var answer = examMemberQuestion.examQuestion.examClosedAnswers[j];

                          for (var k = 0; k < examMemberQuestion.examAnswers.length; k++) {
                            var examAnswer = examMemberQuestion.examAnswers[k];
                            if (examAnswer.examClosedAnswer.id == answer.id) {
                              if (examMemberQuestion.examQuestion.type == 1) {
                                this.checkRadioExamAnswer2(examMemberQuestion.id, answer.id);
                              }
                              if (examMemberQuestion.examQuestion.type == 2) {
                                this.checkBoxExamAnswer2(examMemberQuestion.id, answer.id);
                              }
                            }
                          }
                        }
                      }
                    }
                  }


                } else {
                  this.router.navigate(['/exam/' + this.exam.id + '/results']);
                }
              }
            }
          }
        },
        error => {
          this.message = error["error"]["error"];
        });


    });
  }


  get jte() {
    return this.joinToExamForm.controls;
  }

  joinToExam() {
    if (this.joinToExamForm.invalid) {
      return;
    }

    this.loading = true;
    this.examService.joinToRunningExam(this.exam.id, this.jte.joinToExamPassword.value)
    .pipe(first())
    .subscribe(
      data => {
        this.loading = false;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }


  addAnswers() {
    this.loading = true;
    var lastIdExamMemberQuestion = 0;
    var i = 0;
    this.done = 0;
    this.examAnswerWrapperList = [];

    var answers = $(".answers");
    var length = answers.length;

    for(var index=0; index<length; index++){
      var element = answers.eq(index);
      if(element.is(':checked')){
        if (lastIdExamMemberQuestion != parseInt(element.attr('name'))) {
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_member_question = element.attr('name');
          examAnswerWrapper.id_exam_closed_answers.push(element.val());
          this.examAnswerWrapperList.push(examAnswerWrapper);
          lastIdExamMemberQuestion = parseInt(element.attr('name'));
          i++;
        } else {
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_closed_answers = this.examAnswerWrapperList[i - 1]["id_exam_closed_answers"];
          examAnswerWrapper.id_exam_closed_answers.push(element.val());
          this.examAnswerWrapperList[i - 1]["id_exam_closed_answers"] = examAnswerWrapper.id_exam_closed_answers;
        }
      }
      else{
        if (lastIdExamMemberQuestion != parseInt(element.attr('name'))) {
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_member_question = element.attr('name');
          examAnswerWrapper.id_exam_closed_answers = [];
          this.examAnswerWrapperList.push(examAnswerWrapper);
          lastIdExamMemberQuestion = parseInt(element.attr('name'));
          i++;
        }
      }

      if (index === (length - 1)) {
        this.done = 1;
      }
    }


    this.waitForDone();
  }

  waitForDone() {
    if (this.done == 0) {
      setTimeout(this.waitForDone, 100);
    }
    else {
      this.examService.addAnswers(this.exam.id, this.examAnswerWrapperList)
      .pipe(first())
      .subscribe(
        data => {
          this.loading = false;
          this.ngOnInit();
        },
        error => {
          this.loading = false;
          this.message = error["error"]["error"];
        });
    }
  }

  checkBoxExamAnswer(name: number, value: number) {
    setTimeout(function () {
      $("#" + name + "_" + value).prop('checked', true);
    }, 500);
  }

  checkRadioExamAnswer(name: number, value: number) {
    setTimeout(function () {
      $("#" + name + "__" + value).prop('checked', true);
    }, 500);
  }

  checkBoxExamAnswer2(name: number, value: number) {
    setTimeout(function () {
      $("#" + name + "____" + value).prop('checked', true);
    }, 500);

  }

  checkRadioExamAnswer2(name: number, value: number) {
    setTimeout(function () {
      $("#" + name + "___" + value).prop('checked', true);
    }, 500);
  }

  changeExamMemberPositionManually(position: number) {
    this.examService.changeExamMemberPosition(this.exam.id, position)
    .pipe(first())
    .subscribe(
      data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  refreshTimeToEnd(){
    let date = new Date();
    if(date.getTime() >= parseInt(this.exam.finishTime)){
      this.ngOnInit();
      return;
    }


    let ms = parseInt(this.exam.finishTime) - date.getTime();
    let hours = Math.floor(ms / (1000*60*60));
    let minutes = Math.floor((ms-hours * 1000 * 60 * 60) / (1000*60));
    let seconds = Math.floor((ms-hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000);

    this.timeToEndExam.hours = hours;
    this.timeToEndExam.minutes = minutes;
    this.timeToEndExam.seconds = seconds;

    setTimeout(() => {
      this.refreshTimeToEnd();
    }, 1000);
  }

  checkBlockade(){
    this.examService.getRunningExam(this.idExam)
    .pipe(first())
    .subscribe(
      data => {
        this.examRef = data;
        if(!this.exam.examMembers[0].blocked && this.examRef.examMembers[0].blocked){
          this.ngOnInit();
        }
        if(this.exam.examMembers[0].blocked && !this.examRef.examMembers[0].blocked){
          this.ngOnInit();
        }
        if(!this.exam.finished && this.examRef.finished){
          this.ngOnInit();
        }
      },
      error => {
      });

    setTimeout(() => {
      this.checkBlockade();
    }, 30000);

  }

  refreshTimeToEndQuestion(){
    if((this.exam.type == 3 || this.exam.type == 4) && !this.exam.returnToQuestions){
      if(this.exam.showAllQuestions){
        var fullTime = 0;
        for(var i =0; i<this.exam.examMembers[0].examMemberQuestions.length; i++){
          fullTime += this.exam.examMembers[0].examMemberQuestions[i].examQuestion.answerTime;

          if(i+1 >= this.exam.examMembers[0].examMemberQuestions.length){

            var date = new Date();
            let ms = parseInt(this.exam.startTime) + fullTime*60*1000 - date.getTime();
            let hours = Math.floor(ms / (1000*60*60));
            let minutes = Math.floor((ms-hours * 1000 * 60 * 60) / (1000*60));
            let seconds = Math.floor((ms-hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000);

            if(ms <= 1000){
              this.addAnswers();
            }

            this.timeToEndQuestion.hours = hours;
            this.timeToEndQuestion.minutes = minutes;
            this.timeToEndQuestion.seconds = seconds;
          }
        }
      }else{
        var fullTime = 0;
        for(var i =0; i<this.exam.examMembers[0].examMemberQuestions.length; i++){
          if(this.exam.examMembers[0].position >= this.exam.examMembers[0].examMemberQuestions[i].position)
          {
            fullTime += this.exam.examMembers[0].examMemberQuestions[i].examQuestion.answerTime;
          }

          if(i+1 >= this.exam.examMembers[0].examMemberQuestions.length){

            var date = new Date();
            let ms = parseInt(this.exam.startTime) + fullTime*60*1000 - date.getTime();
            let hours = Math.floor(ms / (1000*60*60));
            let minutes = Math.floor((ms-hours * 1000 * 60 * 60) / (1000*60));
            let seconds = Math.floor((ms-hours * 1000 * 60 * 60 - minutes * 1000 * 60) / 1000);

            if(ms <= 1000){
              this.addAnswers();
            }

            this.timeToEndQuestion.hours = hours;
            this.timeToEndQuestion.minutes = minutes;
            this.timeToEndQuestion.seconds = seconds;
          }
        }
      }
    }

    setTimeout(() => {
      this.refreshTimeToEndQuestion();
    }, 1000);
  }

  refresh(){
    this.ngOnInit();
    setTimeout(this.refresh, 5000);
  }
}
