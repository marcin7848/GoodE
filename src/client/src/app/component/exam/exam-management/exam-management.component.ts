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
          console.log("Nie mozna pobrac!");
        });


      this.examService.getExamFullById(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;
          console.log(this.exam);

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
              console.log("Nie mozna pobrac!");
              this.message = error["error"]["error"];
            });

        },
        error => {
          console.log(error);
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });
    });
  }

  addAllExamQuestions(){
    this.examService.addAllExamQuestions(this.exam.id).
    subscribe(data => {
        console.log("Dodano wszystkie pytania");
        location.reload();
      },
      error => {
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      })
  }

  addQuestionToExam(question: Question){
    this.examService.addQuestionToExam(this.exam.id, question.id).
    subscribe(data => {
        console.log("Dodano pytanie!");
        location.reload();
      },
      error => {
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      })
  }

  deleteExamQuestion(question: ExamQuestion){
    this.examService.deleteExamQuestion(this.exam.id, question.id).
    subscribe(data => {
        console.log("Usunięto pytanie");
        location.reload();
      },
      error => {
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      })
  }

  changeExamQuestionPosition(question: ExamQuestion, position: number){
    this.examService.changeExamQuestionPosition(this.exam.id, question.id, position).
    subscribe(data => {
        console.log("Przeniesiono pytanie");
        location.reload();
      },
      error => {
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      })
  }

  startExam(){
    this.router.navigate(['/exam/'+this.exam.id+'/running/management']);
  }

}
