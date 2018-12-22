import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {QuestionService} from "../../../service/question/question.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {Question} from "../../../model/Question";
import {first} from "rxjs/operators";

declare var jquery:any;
declare var $ :any;

@Component({
  selector: 'app-running-exam-management',
  templateUrl: './running-exam-management.component.html',
  styleUrls: ['./running-exam-management.component.scss']
})
export class RunningExamManagementComponent implements OnInit {

  idExam: number;
  loggedAccount: Account;
  exam: Exam;
  message = "";
  runningProcess = -1;
  initiateJoiningForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService,
              private questionService: QuestionService) { }

  ngOnInit() {
    this.initiateJoiningForm = this.formBuilder.group({
      initiateJoiningPassword: ['', Validators.required],
      initiateJoiningColor: ['', Validators.required]
    });


    this.route.params.subscribe(params => {
      this.idExam = params['idExam'];

      this.accountService.getLoggedAccount().
      subscribe(data => {
          this.loggedAccount = data;
          if(this.loggedAccount.accessRole.role == 'ROLE_STUDENT'){
            this.router.navigate(['/']);
          }
        },
        error => {
          console.log("Nie mozna pobrac!");
        });

      this.examService.getRunningExamManagement(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;
          console.log(this.exam);

          if(this.exam.joining == false){
            this.runningProcess = 0;
          }
          else{
            if(this.exam.started == false){
              this.runningProcess = 1;
            }
            else{
              if(this.exam.finished == false){
                this.runningProcess = 2;
              }
              else{
                this.runningProcess = 3;
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

  get ij() { return this.initiateJoiningForm.controls; }

  initiateJoining(){
    this.examService.initiateJoingToExam(this.exam.id, this.ij.initiateJoiningPassword.value, this.ij.initiateJoiningColor.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Zainicjowano egzamin");
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }

  startExam(){
    let finishTime = $("#finishTimeAtStarting").val();
    if(finishTime == undefined || finishTime == null){
      finishTime = "";
    }

    this.examService.startExam(this.exam.id, finishTime)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Wystartowano egzamin");
        location.reload();
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }

  blockExamMember(examMember){
    this.examService.blockExamMember(this.exam.id, examMember.id, $("#causeOfBlockadeExamMember").val())
    .pipe(first())
    .subscribe(
      data => {
        console.log("Zablokowano/odblokowano uzytkownika");
        location.reload();
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }
}
