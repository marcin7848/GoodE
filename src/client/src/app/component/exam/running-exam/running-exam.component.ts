import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {first} from "rxjs/operators";
import {ExamAnswerWrapper} from "../../../model/ExamAnswerWrapper";

declare var jquery:any;
declare var $ :any;

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

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService) { }

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
          console.log(this.exam);

          if(this.exam.joining == false){
            this.router.navigate(['/']);
          }
          else{
            if(this.exam.examMembers[0].id == 0){
              this.runningProcess = 0;
            }
            else{
              if(this.exam.examMembers[0].blocked){
                this.blockade = 1;
                this.causeOfBlockade = this.exam.examMembers[0].causeOfBlockade;
              }else{
                if(!this.exam.finished){
                  this.runningProcess = 1;
                }else{
                  this.router.navigate(['/exam/'+this.exam.id+'/results']);
                }
              }
            }
          }
        },
        error => {
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });


    });
  }


  get jte() { return this.joinToExamForm.controls; }

  joinToExam(){
    this.examService.joinToRunningExam(this.exam.id, this.jte.joinToExamPassword.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Dolaczono");
        location.reload();
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }


  addAnswers(){
    var lastIdExamMemberQuestion = 0;
    var examAnswerWrapperList = [];
    var i = 0;
    var done = 0;

    var length =  $(".answers").length;
    $(".answers").each(function(index, answer){
      if($(this).is(':checked')){
        if(lastIdExamMemberQuestion != parseInt($(this).attr('name'))){
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_member_question = $(this).attr('name');
          examAnswerWrapper.id_exam_closed_answers.push($(this).val());
          examAnswerWrapperList.push(examAnswerWrapper);
          lastIdExamMemberQuestion = parseInt($(this).attr('name'));
          i++;
        }else{
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_closed_answers = examAnswerWrapperList[i-1]["id_exam_closed_answers"];
          examAnswerWrapper.id_exam_closed_answers.push($(this).val());
          examAnswerWrapperList[i-1]["id_exam_closed_answers"] = examAnswerWrapper.id_exam_closed_answers;
        }
      }
      else{
        if(lastIdExamMemberQuestion != parseInt($(this).attr('name'))) {
          var examAnswerWrapper = new ExamAnswerWrapper();
          examAnswerWrapper.id_exam_member_question = $(this).attr('name');
          examAnswerWrapper.id_exam_closed_answers = [];
          examAnswerWrapperList.push(examAnswerWrapper);
          lastIdExamMemberQuestion = parseInt($(this).attr('name'));
          i++;
        }
      }

      if(index === (length - 1)){
        done = 1;
      }

    });

    function waitForDone(examService: ExamService, exam: Exam, message: string){
      if(done == 0){
        setTimeout(waitForDone, 100);
      }
      else{
        console.log(examAnswerWrapperList);
        examService.addAnswers(exam.id, examAnswerWrapperList)
        .pipe(first())
        .subscribe(
          data => {
            console.log("Dodano odpowiedzi");
            location.reload();
          },
          error => {
            console.log(error);
            console.log("Nie mozna wykonac!");
            message = error["error"]["error"];
          });
      }
    }

    waitForDone(this.examService, this.exam, this.message);
  }

  checkBoxExamAnswer(name: number, value: number){
    $("#"+name+"_"+value).prop('checked', true);
  }

  checkRadioExamAnswer(name: number, value: number){
    $("#"+name+"__"+value).prop('checked', true);
  }

  checkBoxExamAnswer2(name: number, value: number){
    $("#"+name+"____"+value).prop('checked', true);
  }

  checkRadioExamAnswer2(name: number, value: number){
    $("#"+name+"___"+value).prop('checked', true);
  }

  changeExamMemberPositionManually(position: number){
    this.examService.changeExamMemberPosition(this.exam.id, position)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Zmienono pozycje!");
        location.reload();
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }
}
