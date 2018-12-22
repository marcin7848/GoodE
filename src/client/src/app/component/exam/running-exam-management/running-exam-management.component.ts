import { Component, OnInit } from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {QuestionService} from "../../../service/question/question.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {Question} from "../../../model/Question";
import {first} from "rxjs/operators";

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

      this.examService.getRunningExamManagement(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;
          console.log(this.exam);
        },
        error => {
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });


    });


  }

}
