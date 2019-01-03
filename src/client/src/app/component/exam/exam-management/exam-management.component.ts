import { Component, OnInit } from '@angular/core';
import {first} from "rxjs/operators";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {QuestionService} from "../../../service/question/question.service";
import {Question} from "../../../model/Question";
import {ExamQuestion} from "../../../model/ExamQuestion";
import {ClosedAnswer} from "../../../model/ClosedAnswer";
import {ExamClosedAnswer} from "../../../model/ExamClosedAnswer";

@Component({
  selector: 'app-exam-management',
  templateUrl: './exam-management.component.html',
  styleUrls: ['./exam-management.component.scss']
})
export class ExamManagementComponent implements OnInit {

  idExam: number;
  loggedAccount: Account;
  exam: Exam;
  message = "";
  questions: Question[];
  loading = false;



  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService,
              private questionService: QuestionService) { }

  ngOnInit() {
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
        });


      this.examService.getExamFullById(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;

          this.questionService.getQuestions(this.exam.group.id)
          .pipe(first())
          .subscribe(
            data2 => {
              this.questions = data2;
              for(let i=0; i<this.questions.length; i++){
                this.questions[i].question.replace(/\r\n/g, "<br />")
              }
            },
            error => {
              this.message = error["error"]["error"];
            });

        },
        error => {
          this.message = error["error"]["error"];
        });
    });
  }

  addAllExamQuestions(){
    this.loading = true;
    this.examService.addAllExamQuestions(this.exam.id).
    subscribe(data => {
        this.loading = false;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      })
  }

  addQuestionToExam(question: Question){
    this.loading = true;
    this.examService.addQuestionToExam(this.exam.id, question.id).
    subscribe(data => {
        this.loading = false;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      })
  }

  deleteExamQuestion(question: ExamQuestion){
    this.loading = true;
    this.examService.deleteExamQuestion(this.exam.id, question.id).
    subscribe(data => {
        this.loading = false;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      })
  }

  changeExamQuestionPosition(question: ExamQuestion, position: number){
    this.examService.changeExamQuestionPosition(this.exam.id, question.id, position).
    subscribe(data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
      })
  }

  startExam(){
    this.router.navigate(['/exam/'+this.exam.id+'/running/management']);
  }

  changeCorrectExamClosedAnswer(examQuestion: ExamQuestion, examClosedAnswer: ExamClosedAnswer){
    this.examService.changeCorrectExamClosedAnswer(this.exam.id, examQuestion.id, examClosedAnswer.id)
    .pipe(first())
    .subscribe(
      data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
      });
  }

  changeCorrectAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.changeCorrectAnswer(this.exam.group.id, question.id, closedAnswer.id)
    .pipe(first())
    .subscribe(
      data => {
      },
      error => {
        this.message = error["error"]["error"];
      });
  }
}
