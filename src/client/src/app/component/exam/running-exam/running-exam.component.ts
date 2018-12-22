import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {ExamService} from "../../../service/exam/exam.service";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {Exam} from "../../../model/Exam";
import {first} from "rxjs/operators";

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
                this.runningProcess = 1;



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

}
