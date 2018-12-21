import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {GroupService} from "../../../service/group/group.service";
import {first} from "rxjs/operators";
import {Group} from "../../../model/Group";
import {GroupMember} from "../../../model/GroupMember";
import {AccountService} from "../../../service/account/account.service";
import {ExamService} from "../../../service/exam/exam.service";
import {Exam} from "../../../model/Exam";

declare var jquery:any;
declare var $ :any;

@Component({
  selector: 'app-group-view',
  templateUrl: './group-view.component.html',
  styleUrls: ['./group-view.component.scss']
})
export class GroupViewComponent implements OnInit {
  message = "";
  id: number;
  group: Group;
  groupMembers: GroupMember[];
  actionMessage = "";
  exams: Exam[];

  editMode = 0;
  examForm: FormGroup;
  currentExam: Exam;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private groupService: GroupService,
              private accountService: AccountService,
              private examService: ExamService) { }

  ngOnInit() {
    this.examForm = this.formBuilder.group({
      title: ['', Validators.required],
      type: ['', Validators.required],
      difficulty: ['', Validators.required],
      showAllQuestions: ['', Validators.required],
      returnToQuestions: [''],
      sendResultsInstantly: [''],
      showFullResults: [''],
      mixQuestions: [''],
      percentToPass: ['', Validators.required],
      numberOfQuestions: [''],
      maxTime: [''],
    });


    this.route.params.subscribe(params => {
      this.id = params['id'];

      this.groupService.getGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.group = data;
        },
        error => {
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });

      this.groupService.getGroupMembers(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.groupMembers = data;
        },
        error => {
          //nie poberać może też, bo użytkownik nie jest członkiem ani adminem
          console.log(error);
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });

      this.examService.getAllExamsByIdGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.exams = data;
        },
        error => {
          console.log(error);
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });

    });
  }

  get f() { return this.examForm.controls; }

  showAddExam(){
    this.editMode = 1;
    $("#formExam").css("visibility", "visible");
  }

  showEditExam(exam: Exam){
    this.editMode = 2;
    this.currentExam = exam;
    this.f.title.setValue(exam.title);
    this.f.type.setValue(exam.type);
    this.f.difficulty.setValue(exam.difficulty);
    this.f.showAllQuestions.setValue(exam.showAllQuestions);
    this.f.returnToQuestions.setValue(exam.returnToQuestions);
    this.f.sendResultsInstantly.setValue(exam.sendResultsInstantly);
    this.f.showFullResults.setValue(exam.showFullResults);
    this.f.mixQuestions.setValue(exam.mixQuestions);
    this.f.percentToPass.setValue(exam.percentToPass);
    this.f.numberOfQuestions.setValue(exam.numberOfQuestions);
    this.f.maxTime.setValue(exam.maxTime);
    $("#formExam").css("visibility", "visible");
  }

  joinToGroup(idGroup: number){
    this.groupService.joinToTheGroup(idGroup).
    subscribe(data => {
        this.actionMessage = "Dolaczyles do grupy!";
      },
      error => {
        this.actionMessage = error["error"]["error"];
        console.log("Nie mozna pobrac!");
      })
  }

  acceptNewMember(idGroup: number, idGroupMember: number){
    this.groupService.acceptMember(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Zaakceptowano!");
      },
      error => {
        console.log("Nie mozna pobrac!");
      })
  }

  leaveGroup(idGroup: number){
    this.groupService.leaveGroup(idGroup).
    subscribe(data => {
        console.log("Opusciles grupe!");
      },
      error => {
        console.log("Nie mozna pobrac!");
      })
  }

  deleteGroupMember(idGroup: number, idGroupMember: number){
    this.groupService.deleteGroupMember(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Usunieto czlonka grupy!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  promoteToTeacher(idGroup: number, idGroupMember: number){
    this.groupService.promoteToTeacher(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Pomyslnie nadano range nauczyciela!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  reduceToStudent(idGroup: number, idGroupMember: number){
    this.groupService.reduceToStudent(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Pomyslnie zmieniono range na Student!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  deleteGroup(idGroup: number){
    this.groupService.deleteGroup(idGroup).
    subscribe(data => {
        console.log("Pomyslnie usunieto grupe!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }


  addNewExam(){
    this.examService.addNewExam(this.id, this.f.title.value, this.f.type.value, this.f.difficulty.value,
      this.f.showAllQuestions.value, this.f.returnToQuestions.value, this.f.sendResultsInstantly.value, this.f.showFullResults.value,
      this.f.mixQuestions.value, this.f.percentToPass.value, this.f.numberOfQuestions.value, this.f.maxTime.value)
    .pipe(first())
    .subscribe(
      data => {
        location.reload();
      },
      error => {
        console.log("Nie mozna pobrac!");
        this.message = error["error"]["error"];
      });
  }

  editExam(exam: Exam){
    this.examService.editExam(exam.id, this.f.title.value, this.f.type.value, this.f.difficulty.value,
      this.f.showAllQuestions.value, this.f.returnToQuestions.value, this.f.sendResultsInstantly.value, this.f.showFullResults.value,
      this.f.mixQuestions.value, this.f.percentToPass.value, this.f.numberOfQuestions.value, this.f.maxTime.value)
    .pipe(first())
    .subscribe(
      data => {
        location.reload();
      },
      error => {
        console.log("Nie mozna pobrac!");
        this.message = error["error"]["error"];
      });
  }

  goToManageExam(exam){
    this.router.navigate(['/exam/'+exam.id+'/management']);
  }

}
