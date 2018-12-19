import { Component, OnInit } from '@angular/core';
import {first} from "rxjs/operators";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";

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


  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.idExam = params['idExam'];

      this.accountService.getLoggedAccount().
      subscribe(data => {
          this.loggedAccount = data;
          if(this.loggedAccount.accessRole.role == 'ROLE_STUDENT'){
            this.router.navigate(['/exam/']);
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
        },
        error => {
          console.log(error);
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });





    });
  }

}
