import {Component, OnInit} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Exam} from "../../../model/Exam";
import {Results} from "../../../model/Results";
import {Account} from "../../../model/Account";
import {GroupMember} from "../../../model/GroupMember";
import {MatTableDataSource} from "@angular/material";

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
  tookPartInExam = false;

  //results details variable
  firstName: string;
  lastName: string;
  registerNo: number;
  points: number;
  maxPoints: number;
  percentResult: number;
  examMemberResult: Results;
  blocked: boolean;
  causeOfBlockade: string;

  dataSourceOfResultsForAllExamMembers = new MatTableDataSource(this.resultsForAllExamMembers);
  displayedColumnsResultsForAllExamMembers: string[] = ['username', 'firstName', 'lastName', 'registerNo', 'points', 'result', 'blocked', 'details'];

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
                      if (i + 1 >= this.resultsForAllExamMembers.length) {
                        this.dataSourceOfResultsForAllExamMembers = new MatTableDataSource(this.resultsForAllExamMembers);

                        this.dataSourceOfResultsForAllExamMembers.filterPredicate = function (data, filter): boolean {
                          if (data.exam.examMembers[0].account.username.toLowerCase().includes(filter)) {
                            return true;
                          }
                          if (data.exam.examMembers[0].account.firstName.toLowerCase().includes(filter)) {
                            return true;
                          }
                          if (data.exam.examMembers[0].account.lastName.toLowerCase().includes(filter)) {
                            return true;
                          }
                          if (data.exam.examMembers[0].account.register_no.toString().toLowerCase().includes(filter)) {
                            return true;
                          }

                          if (data.points.toString().toLowerCase().includes(filter)) {
                            return true;
                          }

                          if (data.maxPoints.toString().toLowerCase().includes(filter)) {
                            return true;
                          }

                          if (data.percentResult.toString().toLowerCase().includes(filter)) {
                            return true;
                          }

                          if (data.exam.examMembers[0].blocked.toString().toLowerCase().includes(filter)) {
                            return true;
                          }

                          return false;
                        };

                      }
                    }
                  },
                  error => {
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

                    if (this.exam.examMembers[0].id != 0) {
                      this.tookPartInExam = true;
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
                            this.message = error["error"]["error"];
                          });
                      }
                    }
                  }
                }
              }
            },
            error => {
              this.message = error["error"]["error"];
            });
        },
        error => {
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
        this.ngOnInit()
      },
      error => {
        this.message = error["error"]["error"];
      });
  }

  showDetails(result: Results) {
    this.firstName = result.exam.examMembers[0].account.firstName;
    this.lastName = result.exam.examMembers[0].account.lastName;
    this.registerNo = result.exam.examMembers[0].account.register_no;
    this.points = result.points;
    this.maxPoints = result.maxPoints;
    this.percentResult = result.percentResult;
    this.examMemberResult = result;
    console.log(this.examMemberResult);
    this.blocked = result.exam.examMembers[0].blocked;
    this.causeOfBlockade = result.exam.examMembers[0].causeOfBlockade;
    this.formMode = 1;
  }

  applyFilterResultsForAllExamMembers(filterValue: string) {
    this.dataSourceOfResultsForAllExamMembers.filter = filterValue.trim().toLowerCase();
  }

  closeDetails() {
    this.formMode = 0;
  }
}
