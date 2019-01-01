import {Component, OnInit} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Exam} from "../../../model/Exam";
import {Results} from "../../../model/Results";
import {Account} from "../../../model/Account";

declare var jquery: any;
declare var $: any;


@Component({
  selector: 'app-exam-results',
  templateUrl: './exam-results.component.html',
  styleUrls: ['./exam-results.component.scss']
})
export class ExamResultsComponent implements OnInit {

  message = "";
  exam: Exam;
  idExam: number;
  results: Results;
  resultsProcess = 0;
  loggedAccount: Account;
  resultsForAllExamMembers: Results[];
  formMode = 0;
  loading = false;
  submitted = false;

  //results details variable
  firstName: string;
  lastName: string;
  examMemberResult: Results;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.idExam = params['idExam'];


      this.accountService.getLoggedAccount().subscribe(data => {
          this.loggedAccount = data;
          this.examService.getRunningExam(this.idExam)
          .pipe(first())
          .subscribe(
            data => {
              this.exam = data;

              if (this.loggedAccount.accessRole.role == 'ROLE_ADMIN' || this.loggedAccount.accessRole.role == 'ROLE_TEACHER') {
                this.examService.getResultsForAllExamMembers(this.exam.id)
                .pipe(first())
                .subscribe(
                  data => {
                    this.resultsForAllExamMembers = data['listOfResultsForAllExamMembers'];
                    for (let i = 0; i < this.resultsForAllExamMembers.length; i++) {
                      this.resultsForAllExamMembers[i].percentResult = Math.round((this.resultsForAllExamMembers[i].points / this.resultsForAllExamMembers[i].maxPoints) * 100);
                    }

                  },
                  error => {
                    console.log(error);
                    console.log("Nie mozna wykonac!");
                    this.message = error["error"]["error"];
                  });
              }

              if (this.exam.examMembers[0].id == 0 && this.loggedAccount.accessRole.role != 'ROLE_ADMIN' && this.loggedAccount.accessRole.role != 'ROLE_TEACHER') {
                this.router.navigate(['/']);
              }
              else {
                if (this.exam.finished) {
                  this.resultsProcess = 1;

                  if (this.exam.rated) {
                    this.resultsProcess = 2;

                    this.examService.getResults(this.exam.id)
                    .pipe(first())
                    .subscribe(
                      data => {
                        this.results = data;
                        this.results.points = parseInt(data['points']);
                        this.results.maxPoints = parseInt(data['maxPoints']);
                        this.results.examMemberQuestionResults = data['examMemberQuestionResults'] ? data['examMemberQuestionResults'] : null;
                        this.results.percentResult = Math.round((this.results.points / this.results.maxPoints) * 100);
                        this.results.exam = data['exam'] ? data['exam'] : null;
                      },
                      error => {
                        console.log(error);
                        console.log("Nie mozna wykonac!");
                        this.message = error["error"]["error"];
                      });

                    if (this.exam.showFullResults) {
                      this.examService.getResults(this.exam.id)
                      .pipe(first())
                      .subscribe(
                        data => {
                          this.results.points = parseInt(data['points']);
                          this.results.maxPoints = parseInt(data['maxPoints']);
                          this.results.percentResult = Math.round((this.results.points / this.results.maxPoints) * 100);
                        },
                        error => {
                          console.log(error);
                          console.log("Nie mozna wykonac!");
                          this.message = error["error"]["error"];
                        });
                    }
                  }
                }

              }
            },
            error => {
              console.log("Nie mozna pobrac!");
              this.message = error["error"]["error"];
            });


        },
        error => {
          console.log("Nie mozna pobrac!");
        });
    });
  }

  checkBoxExamAnswer(name: number, value: number, correct: boolean) {
    $("#" + name + "_" + value).prop('checked', true);
    if (correct) {
      $("#" + name + "_" + value + "_correct").css('background-color', 'green');
    } else {
      $("#" + name + "_" + value + "_correct").css('background-color', 'red');
    }

  }

  checkRadioExamAnswer(name: number, value: number, correct: boolean) {
    $("#" + name + "__" + value).prop('checked', true);
    if (correct) {
      $("#" + name + "_" + value + "_correct").css('background-color', 'green');
    } else {
      $("#" + name + "_" + value + "_correct").css('background-color', 'red');
    }
  }

  checkBoxExamAnswer2(name: number, value: number, correct: boolean) {
    $("#" + name + "_" + value + "_2").prop('checked', true);
    if (correct) {
      $("#" + name + "_" + value + "_correct2").css('background-color', 'green');
    } else {
      $("#" + name + "_" + value + "_correct2").css('background-color', 'red');
    }

  }

  checkRadioExamAnswer2(name: number, value: number, correct: boolean) {
    $("#" + name + "__" + value + "_2").prop('checked', true);
    if (correct) {
      $("#" + name + "_" + value + "_correct2").css('background-color', 'green');
    } else {
      $("#" + name + "_" + value + "_correct2").css('background-color', 'red');
    }
  }

  rateExam() {
    this.examService.rateExam(this.exam.id)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Oceniono egzamin!");
        location.reload();
      },
      error => {
        console.log(error);
        console.log("Nie mozna wykonac!");
        this.message = error["error"]["error"];
      });
  }

  showDetails(result: Results){
    this.firstName = result.exam.examMembers[0].account.firstName;
    this.lastName = result.exam.examMembers[0].account.lastName;
    this.examMemberResult = result;


    $("#formResultDetails").css("visibility", "visible");
  }
}
