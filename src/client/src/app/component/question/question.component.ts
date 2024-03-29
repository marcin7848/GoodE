import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {GroupService} from "../../service/group/group.service";
import {AccountService} from "../../service/account/account.service";
import {QuestionService} from "../../service/question/question.service";
import {first} from "rxjs/operators";
import {Group} from "../../model/Group";
import {Question} from "../../model/Question";
import {ClosedAnswer} from "../../model/ClosedAnswer";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

declare var jquery: any;
declare var $: any;

@Component({
  selector: 'app-question',
  templateUrl: './question.component.html',
  styleUrls: ['./question.component.scss']
})
export class QuestionComponent implements OnInit {

  addNewQuestionForm: FormGroup;
  answerForm: FormGroup;
  message="";
  idGroup: number;
  group: Group;
  questions: Question[];
  editMode = 0;
  currentQuestion: Question;
  currentClosedAnswer: ClosedAnswer;
  formMode = 0;
  questionTextarea = "";
  loading = false;
  messageError = "";
  closedAnswerTextarea = "";

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private questionService: QuestionService,
              private groupService: GroupService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.addNewQuestionForm = this.formBuilder.group({
      addQuestion: ['', Validators.required],
      addType: ['', Validators.required],
      addDifficulty: ['', Validators.required],
      addPoints: ['', Validators.required],
      addAnswerTime: ['']
    });

    this.answerForm = this.formBuilder.group({
      closedAnswer: ['', Validators.required],
      correct: [false, Validators.required]
    });

    this.route.params.subscribe(params => {
      this.idGroup = params['idGroup'];

      this.groupService.getGroup(this.idGroup)
      .pipe(first())
      .subscribe(
        data => {
          this.group = data;
        },
        error => {
          this.message = error["error"]["error"];
        });

      this.questionService.getQuestions(this.idGroup)
      .pipe(first())
      .subscribe(
        data => {
          this.questions = data;
          for(var i=0; i<this.questions.length; i++){
            this.questions[i].question.replace(/\r\n/g, "<br />")
          }
        },
        error => {
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });

    });

    $(document).delegate('#questionTextarea', 'keydown', function(e) {
      var keyCode = e.keyCode || e.which;
      if (keyCode == 9) {
        e.preventDefault();
        var start = this.selectionStart;
        var end = this.selectionEnd;

        $(this).val($(this).val().substring(0, start)
          + "\t"
          + $(this).val().substring(end));

        this.selectionStart =
          this.selectionEnd = start + 1;
      }
    });

    $(document).delegate('#closedAnswerTextarea', 'keydown', function(e) {
      var keyCode = e.keyCode || e.which;
      if (keyCode == 9) {
        e.preventDefault();
        var start = this.selectionStart;
        var end = this.selectionEnd;

        $(this).val($(this).val().substring(0, start)
          + "\t"
          + $(this).val().substring(end));

        this.selectionStart =
          this.selectionEnd = start + 1;
      }
    });
  }

  get f() { return this.addNewQuestionForm.controls; }
  get a() { return this.answerForm.controls; }

  showAddQuestion(){
    this.formMode = 1;
    this.editMode = 1;
    this.messageError = "";
    this.loading = false;
    this.questionTextarea = "";
  }

  showEditQuestion(question: Question){
    this.messageError = "";
    this.loading = false;
    this.formMode = 1;
    this.editMode = 2;
    this.currentQuestion = question;

    let questionString = question.question;
    this.questionTextarea = questionString;
    questionString = questionString.replace(/<br\s*[\/]?>/gi,"\n");
    questionString = questionString.replace(/&nbsp;&nbsp;&nbsp;&nbsp;/gi,"\t");

    this.f.addQuestion.setValue(questionString);
    this.f.addType.setValue(question.type);
    this.f.addDifficulty.setValue(question.difficulty);
    this.f.addPoints.setValue(question.points);
    this.f.addAnswerTime.setValue(question.answerTime);
  }

  showAddAnswer(question: Question){
    this.clearAllAnswerBox();
    this.formMode = 0;
    this.editMode = 1;
    this.messageError = "";
    this.loading = false;
    this.closedAnswerTextarea = "";
    this.currentQuestion = question;
    $('#answerBox_'+question.id).css('display', '');
    $('#answerBox_'+question.id).toggleClass('open');
  }


  showEditAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.clearAllAnswerBox();
    this.formMode = 0;
    this.editMode = 2;
    this.messageError = "";
    this.loading = false;
    this.currentQuestion = question;
    this.currentClosedAnswer = closedAnswer;

    let closedAnswerString = closedAnswer.closedAnswer;
    this.closedAnswerTextarea = closedAnswerString;
    closedAnswerString = closedAnswerString.replace(/<br\s*[\/]?>/gi,"\n");
    closedAnswerString = closedAnswerString.replace(/&nbsp;&nbsp;&nbsp;&nbsp;/gi,"\t");
    this.a.closedAnswer.setValue(closedAnswerString);
    this.a.correct.setValue(closedAnswer.correct);

    $('#answerBox_'+question.id).css('display', '');
  }

  addNewQuestion(){
    if (this.addNewQuestionForm.invalid) {
      return;
    }
    this.loading = true;

    this.questionService.addNewQuestion(this.idGroup, this.questionTextarea, this.f.addType.value, this.f.addDifficulty.value,
      this.f.addPoints.value, this.f.addAnswerTime.value)
    .pipe(first())
    .subscribe(
      data => {
        this.formMode = 0;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageError = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  editQuestion(question: Question){
    if (this.addNewQuestionForm.invalid) {
      return;
    }
    this.loading = true;

    this.questionService.editQuestion(this.idGroup, question.id, this.questionTextarea, this.f.addType.value, this.f.addDifficulty.value,
      this.f.addPoints.value, this.f.addAnswerTime.value)
    .pipe(first())
    .subscribe(
      data => {
        this.formMode = 0;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageError = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  deleteQuestion(question: Question){
    this.questionService.deleteQuestion(this.idGroup, question.id)
    .pipe(first())
    .subscribe(
      data => {
        this.ngOnInit()
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }


  addNewAnswer(question: Question){
    if (this.answerForm.invalid) {
      return;
    }
    this.loading = true;

    this.questionService.addNewAnswer(this.idGroup, question.id, this.closedAnswerTextarea, this.a.correct.value)
    .pipe(first())
    .subscribe(
      data => {
        this.formMode = 0;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageError = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  editAnswer(question: Question, closedAnswer: ClosedAnswer){
    if (this.answerForm.invalid) {
      return;
    }
    this.loading = true;

    this.questionService.editAnswer(this.idGroup, question.id, closedAnswer.id, this.closedAnswerTextarea, this.a.correct.value)
    .pipe(first())
    .subscribe(
      data => {
        this.formMode = 0;
        this.ngOnInit();
      },
      error => {
        this.loading = false;
        this.messageError = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  deleteAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.deleteAnswer(this.idGroup, question.id, closedAnswer.id)
    .pipe(first())
    .subscribe(
      data => {
        this.ngOnInit()
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  changeCorrectAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.changeCorrectAnswer(this.idGroup, question.id, closedAnswer.id)
    .pipe(first())
    .subscribe(
      data => {
      },
      error => {
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  cancel(){
    this.formMode = 0;
    this.clearAllAnswerBox();
  }

  questionTextareaChanged(event: string){
    event = event.replace(/\r\n|\r|\n/g,"<br />");
    event = event.replace(/\t/g,"&nbsp;&nbsp;&nbsp;&nbsp;");
    this.questionTextarea = event;
  }

  closedAnswerTextareaChanged(event: string){
    event = event.replace(/\r\n|\r|\n/g,"<br />");
    event = event.replace(/\t/g,"&nbsp;&nbsp;&nbsp;&nbsp;");
    this.closedAnswerTextarea = event;
  }

  clearAllAnswerBox(){
    for(var i=0; i<this.questions.length; i++){
      $('#answerBox_'+this.questions[i].id).css('display', 'none');
    }
  }

}
