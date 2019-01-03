import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {QuestionService} from "../../../service/question/question.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {Question} from "../../../model/Question";
import {first} from "rxjs/operators";
import {GroupMember} from "../../../model/GroupMember";
import {MatTableDataSource} from "@angular/material";
import {ExamMember} from "../../../model/ExamMember";

declare var jquery: any;
declare var $: any;

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
  loading = false;

  startExamForm: FormGroup;

  examMembers: ExamMember[];
  dataSourceOfExamMembers = new MatTableDataSource(this.examMembers);
  displayedColumnsExamMembers: string[] = ['idExamMember', 'username', 'firstName', 'lastName', 'registerNo', 'blockade'];

  timeToEndExam = {
    hours: 0,
    minutes: 0,
    seconds: 0
  };

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private examService: ExamService,
              private accountService: AccountService,
              private questionService: QuestionService) {
  }

  ngOnInit() {
    this.initiateJoiningForm = this.formBuilder.group({
      initiateJoiningPassword: ['', Validators.required],
      initiateJoiningColor: ['', Validators.required]
    });

    this.startExamForm = this.formBuilder.group({
      finishTime: ['', Validators.required]
    });

    this.route.params.subscribe(params => {
      this.idExam = params['idExam'];

      this.accountService.getLoggedAccount().subscribe(data => {
          this.loggedAccount = data;
          if (this.loggedAccount.accessRole.role == 'ROLE_STUDENT') {
            this.router.navigate(['/']);
          }
        },
        error => {
        });

      this.examService.getRunningExamManagement(this.idExam)
      .pipe(first())
      .subscribe(
        data => {
          this.exam = data;

          this.refreshTimeToEnd();

          this.examMembers = [];
          this.examMembers = this.exam.examMembers;

          this.dataSourceOfExamMembers = new MatTableDataSource(this.examMembers);

          this.dataSourceOfExamMembers.filterPredicate = function (data, filter): boolean {
            if (data.account.username.toLowerCase().includes(filter)) {
              return true;
            }
            if (data.account.firstName.toLowerCase().includes(filter)) {
              return true;
            }
            if (data.account.lastName.toLowerCase().includes(filter)) {
              return true;
            }
            if (data.account.register_no.toString().toLowerCase().includes(filter)) {
              return true;
            }
            if (data.id.toString().toLowerCase().includes(filter)) {
              return true;
            }
            return false;
          };

          if (this.exam.joining == false) {
            this.runningProcess = 0;
          }
          else {
            if (this.exam.started == false) {
              this.runningProcess = 1;
              setTimeout(() => {
                this.ngOnInit();
              }, 10000);
            }
            else {
              if (this.exam.finished == false) {
                this.runningProcess = 2;
                setTimeout(() => {
                  this.ngOnInit();
                }, 60000*5);
              }
              else {
                this.router.navigate(['/exam/' + this.exam.id + '/results']);
              }
            }
          }
        },
        error => {
          this.message = error["error"]["error"];
        });
    });
  }

  get ij() {
    return this.initiateJoiningForm.controls;
  }

  get se() {
    return this.startExamForm.controls;
  }

  initiateJoining() {
    if (this.initiateJoiningForm.invalid) {
      return;
    }

    this.loading = true;

    this.examService.initiateJoingToExam(this.exam.id, this.ij.initiateJoiningPassword.value, this.ij.initiateJoiningColor.value)
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

  startExam() {
    this.loading = true;
    let finishTime = $("#finishTimeAtStarting").val();
    if (finishTime == undefined || finishTime == null) {
      finishTime = "";
    }

    let time = new Date();

    let date = new Date(time.getTime() + 1000 * 60 * finishTime);
    let convertedDate = date.getFullYear() + "-" + date.getMonth()+1 + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + ".00";

    this.examService.startExam(this.exam.id, convertedDate)
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

  blockExamMember(examMember) {
    this.loading = true;
    this.examService.blockExamMember(this.exam.id, examMember.id, $("#causeOfBlockadeExamMember").val())
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

  finishExam() {
    this.loading = true;

    this.examService.finishExam(this.exam.id).subscribe(data => {
        this.router.navigate(['/exam/' + this.exam.id + '/results']);
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      });
  }

  applyFilterExamMembers(filterValue: string) {
    this.dataSourceOfExamMembers.filter = filterValue.trim().toLowerCase();
  }

  refreshTimeToEnd(){
    let date = new Date();
    if(date.getTime() >= parseInt(this.exam.finishTime)){
      $("#finishExamButton").click();
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
}
