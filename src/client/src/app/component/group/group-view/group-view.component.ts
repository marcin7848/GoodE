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
import {MatDialog, MatSnackBar, MatTableDataSource} from '@angular/material';
import {TranslateService} from "@ngx-translate/core";
import {DialogBlockExamMemberComponent} from "../../exam/running-exam-management/dialog-block-exam-member/dialog-block-exam-member.component";
import {DialogGroupDeleteComponent} from "./dialog-group-delete/dialog-group-delete.component";

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
  messageExam = "";
  exams: Exam[];

  loading = false;
  submitted = false;

  formMode = 0;
  editMode = 0;
  examForm: FormGroup;
  currentExam: Exam;

  examFromDraftForm: FormGroup;

  groupMembersOfTeachers: GroupMember[];
  dataSourceOfGroupMembersOfTeachers = new MatTableDataSource(this.groupMembersOfTeachers);
  displayedColumnsGroupMembersOfTeachers: string[] = ['username', 'firstName', 'lastName', 'actions'];

  groupMembersOfStudents: GroupMember[];
  dataSourceOfGroupMembersOfStudents = new MatTableDataSource(this.groupMembersOfStudents);
  displayedColumnsGroupMembersOfStudents: string[] = ['username', 'firstName', 'lastName', 'registerNo', 'actions'];

  groupMembersWaiting: GroupMember[];
  dataSourceOfGroupMembersWaiting = new MatTableDataSource(this.groupMembersWaiting);
  displayedColumnsGroupMembersWaiting: string[] = ['username', 'firstName', 'lastName', 'registerNo', 'actions'];

  examsNotFinished: Exam[];
  dataSourceOfExams = new MatTableDataSource(this.examsNotFinished);
  displayedColumnsExams: string[] = ['title', 'actions'];

  examsFinished: Exam[];
  dataSourceOfFinishedExams = new MatTableDataSource(this.examsFinished);
  displayedColumnsFinishedExams: string[] = ['title', 'actions'];

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private groupService: GroupService,
              private accountService: AccountService,
              private examService: ExamService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar,
              public dialog: MatDialog) {}

  ngOnInit() {
    this.examForm = this.formBuilder.group({
      title: ['', Validators.required],
      type: ['', Validators.required],
      difficulty: ['', Validators.required],
      showAllQuestions: [false, Validators.required],
      returnToQuestions: [false],
      sendResultsInstantly: [false],
      showFullResults: [false],
      mixQuestions: [false],
      percentToPass: ['', Validators.required],
      numberOfQuestions: [''],
      maxTime: [''],
      draft: [false]
    });

    this.examFromDraftForm = this.formBuilder.group({
      draft: ['', Validators.required]
    });

    this.route.params.subscribe(params => {
      this.id = params['id'];

      this.groupService.getGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.group = data;
          if(this.group.groupMembers[0].accessRole.role != 'ROLE_ADMIN' && this.group.groupMembers[0].accessRole.role != 'ROLE_TEACHER'){
            this.displayedColumnsGroupMembersOfTeachers = ['username', 'firstName', 'lastName'];
            this.displayedColumnsGroupMembersOfStudents = ['username', 'registerNo'];
            this.displayedColumnsGroupMembersWaiting = [];
          }
        },
        error => {
          this.message = error["error"]["error"];
        });

      this.groupService.getGroupMembers(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.groupMembers = data;
          this.groupMembersOfTeachers = [];
          this.groupMembersOfStudents = [];
          this.groupMembersWaiting = [];

          for(let i=0; i<this.groupMembers.length; i++){
            if((this.groupMembers[i].accessRole.role == 'ROLE_ADMIN' || this.groupMembers[i].accessRole.role == 'ROLE_TEACHER') && this.groupMembers[i].accepted){
              this.groupMembersOfTeachers.push(this.groupMembers[i]);
            }

            if(this.groupMembers[i].accessRole.role == 'ROLE_STUDENT' && this.groupMembers[i].accepted){
              this.groupMembersOfStudents.push(this.groupMembers[i]);
            }

            if(!this.groupMembers[i].accepted){
              this.groupMembersWaiting.push(this.groupMembers[i]);
            }

            if(i+1 >= this.groupMembers.length){
              this.dataSourceOfGroupMembersOfTeachers = new MatTableDataSource(this.groupMembersOfTeachers);
              this.dataSourceOfGroupMembersOfStudents = new MatTableDataSource(this.groupMembersOfStudents);
              this.dataSourceOfGroupMembersWaiting = new MatTableDataSource(this.groupMembersWaiting);

              this.dataSourceOfGroupMembersOfTeachers.filterPredicate = function(data, filter): boolean {
                if(data.account.username.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.firstName.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.lastName.toLowerCase().includes(filter)){
                  return true;
                }
                return false;
              };

              this.dataSourceOfGroupMembersOfStudents.filterPredicate = function(data, filter): boolean {
                if(data.account.username.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.firstName.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.lastName.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.register_no.toString().toLowerCase().includes(filter)){
                  return true;
                }

                return false;
              };

              this.dataSourceOfGroupMembersWaiting.filterPredicate = function(data, filter): boolean {
                if(data.account.username.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.firstName.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.lastName.toLowerCase().includes(filter)){
                  return true;
                }
                if(data.account.register_no.toString().toLowerCase().includes(filter)){
                  return true;
                }

                return false;
              };

            }
          }
        },
        error => {
        });

      this.examService.getAllExamsByIdGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.exams = data;

          this.examsNotFinished = [];
          this.examsFinished = [];

          for(let i=0; i<this.exams.length; i++) {
            if (!this.exams[i].finished) {
              if(!this.exams[i].draft) {
                this.examsNotFinished.push(this.exams[i]);
              }else{
                if(this.group.groupMembers[0].accessRole.role=='ROLE_ADMIN' || this.group.groupMembers[0].accessRole.role=='ROLE_TEACHER'){
                  this.examsNotFinished.push(this.exams[i]);
                }
              }
            }

            if (this.exams[i].finished) {
              this.examsFinished.push(this.exams[i]);
            }

            if (i + 1 >= this.exams.length) {
              this.dataSourceOfExams = new MatTableDataSource(this.examsNotFinished);
              this.dataSourceOfFinishedExams = new MatTableDataSource(this.examsFinished);

              this.dataSourceOfExams.filterPredicate = function (data, filter): boolean {
                return data.title.toLowerCase().includes(filter);
              };

              this.dataSourceOfFinishedExams.filterPredicate = function (data, filter): boolean {
                return data.title.toLowerCase().includes(filter);
              };
            }
          }
        },
        error => {
          this.message = error["error"]["error"];
        });

    });
  }

  get f() { return this.examForm.controls; }

  get g() { return this.examFromDraftForm.controls; }

  showAddExam(){
    this.messageExam = "";
    this.editMode = 0;
    this.formMode = 1;
    this.fillFields();
  }

  showAddExamFromDraft(){
    this.messageExam = "";
    this.editMode = 0;
    this.formMode = 2;
  }

  fillFields(){
    this.f.title.setValue("");
    this.f.type.setValue("");
    this.f.difficulty.setValue("");
    this.f.showAllQuestions.setValue(false);
    this.f.returnToQuestions.setValue(false);
    this.f.sendResultsInstantly.setValue(false);
    this.f.showFullResults.setValue(false);
    this.f.mixQuestions.setValue(false);
    this.f.percentToPass.setValue("");
    this.f.numberOfQuestions.setValue("");
    this.f.maxTime.setValue("");
    this.f.draft.setValue(false);
  }

  showEditExam(exam: Exam){
    this.messageExam = "";
    this.editMode = 1;
    this.formMode = 1;
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
    this.f.draft.setValue(exam.draft);
  }

  joinToGroup(idGroup: number){
    this.groupService.joinToTheGroup(idGroup).
    subscribe(data => {
        this.message = "";
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }

  acceptNewMember(idGroup: number, idGroupMember: number){
    this.groupService.acceptMember(idGroup, idGroupMember).
    subscribe(data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }

  leaveGroup(idGroup: number){
    this.groupService.leaveGroup(idGroup).
    subscribe(data => {
        this.ngOnInit();
      },
      error => {
      })
  }

  deleteGroupMember(idGroup: number, idGroupMember: number){
    this.groupService.deleteGroupMember(idGroup, idGroupMember).
    subscribe(data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }

  promoteToTeacher(idGroup: number, idGroupMember: number){
    this.groupService.promoteToTeacher(idGroup, idGroupMember).
    subscribe(data => {
       this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }

  reduceToStudent(idGroup: number, idGroupMember: number){
    this.groupService.reduceToStudent(idGroup, idGroupMember).
    subscribe(data => {
        this.ngOnInit();
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }

  deleteGroup(idGroup: number){
    this.groupService.deleteGroup(idGroup).
    subscribe(data => {
        this.router.navigate(['/group']);
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      })
  }


  addNewExam(){
    this.submitted = true;

    if (this.examForm.invalid) {
      return;
    }

    this.loading = true;

    this.examService.addNewExam(this.id, this.f.title.value, this.f.type.value, this.f.difficulty.value,
      this.f.showAllQuestions.value, this.f.returnToQuestions.value, this.f.sendResultsInstantly.value, this.f.showFullResults.value,
      this.f.mixQuestions.value, this.f.percentToPass.value, this.f.numberOfQuestions.value, this.f.maxTime.value, this.f.draft.value)
    .pipe(first())
    .subscribe(
      data => {
        this.messageExam = "";
        this.loading = false;
        this.editMode = 0;
        this.formMode = 0;
        this.fillFields();
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageExam = error["error"]["error"];
        this.snackBar.open(this.messageExam, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  editExam(exam: Exam){
    this.submitted = true;

    if (this.examForm.invalid) {
      return;
    }

    this.loading = true;

    this.examService.editExam(exam.id, this.f.title.value, this.f.type.value, this.f.difficulty.value,
      this.f.showAllQuestions.value, this.f.returnToQuestions.value, this.f.sendResultsInstantly.value, this.f.showFullResults.value,
      this.f.mixQuestions.value, this.f.percentToPass.value, this.f.numberOfQuestions.value, this.f.maxTime.value, this.f.draft.value)
    .pipe(first())
    .subscribe(
      data => {
        this.messageExam = "";
        this.loading = false;
        this.editMode = 0;
        this.formMode = 0;
        this.fillFields();
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageExam = error["error"]["error"];
        this.snackBar.open(this.messageExam, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  goToManageExam(exam){
    this.router.navigate(['/exam/'+exam.id+'/management']);
  }

  joinToExam(exam){
    this.router.navigate(['/exam/'+exam.id+'/running']);
  }

  goToResults(exam){
    this.router.navigate(['/exam/'+exam.id+'/results']);
  }

  applyFilterTeachers(filterValue: string) {
    this.dataSourceOfGroupMembersOfTeachers.filter = filterValue.trim().toLowerCase();
  }

  applyFilterStudents(filterValue: string) {
    this.dataSourceOfGroupMembersOfStudents.filter = filterValue.trim().toLowerCase();
  }

  applyFilterWaiting(filterValue: string) {
    this.dataSourceOfGroupMembersWaiting.filter = filterValue.trim().toLowerCase();
  }

  applyFilterExams(filterValue: string) {
    this.dataSourceOfExams.filter = filterValue.trim().toLowerCase();
  }

  applyFilterFinishedExams(filterValue: string) {
    this.dataSourceOfFinishedExams.filter = filterValue.trim().toLowerCase();
  }

  cancelFormExam(){
    this.editMode = 0;
    this.formMode = 0;
  }

  openDialogDeleteGroup(): void {
    const dialogRef = this.dialog.open(DialogGroupDeleteComponent, {
      width: '400px',
      data: {idGroup: this.group.id, name: this.group.name}
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result != undefined)
      {
        this.deleteGroup(result);
      }
    });
  }

  addNewExamFromDraft(){
    this.submitted = true;

    if (this.examFromDraftForm.invalid) {
      return;
    }

    this.loading = true;

    this.examService.addNewExamFromDraft(this.id, this.g.draft.value)
    .pipe(first())
    .subscribe(
      data => {
        this.messageExam = "";
        this.loading = false;
        this.editMode = 0;
        this.formMode = 0;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageExam = error["error"]["error"];
        this.snackBar.open(this.messageExam, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

}
