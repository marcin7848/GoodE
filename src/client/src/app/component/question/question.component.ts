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

declare var jquery:any;
declare var $ :any;

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

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private questionService: QuestionService,
              private groupService: GroupService) { }

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
      correct: ['', Validators.required]
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
          console.log("Nie mozna pobrac!");
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
  }

  get f() { return this.addNewQuestionForm.controls; }
  get a() { return this.answerForm.controls; }

  showAddQuestion(){
    $("#formAnswer").css("visibility", "hidden");
    this.editMode = 1;
    $("#formQuestion").css("visibility", "visible");
  }

  showEditQuestion(question: Question){
    $("#formAnswer").css("visibility", "hidden");
    this.editMode = 2;
    this.currentQuestion = question;
    this.f.addQuestion.setValue(question.question);
    this.f.addType.setValue(question.type);
    this.f.addDifficulty.setValue(question.difficulty);
    this.f.addPoints.setValue(question.points);
    this.f.addAnswerTime.setValue(question.answerTime);
    $("#formQuestion").css("visibility", "visible");
  }

  showAddAnswer(question: Question){
    $("#formQuestion").css("visibility", "hidden");
    this.editMode = 1;
    this.currentQuestion = question;
    $("#formAnswer").css("visibility", "visible");
  }

  showEditAnswer(question: Question, closedAnswer: ClosedAnswer){
    $("#formQuestion").css("visibility", "hidden");
    this.editMode = 2;
    this.currentQuestion = question;
    this.currentClosedAnswer = closedAnswer;
    this.a.closedAnswer.setValue(closedAnswer.closedAnswer);
    this.a.correct.setValue(closedAnswer.correct);
    $("#formAnswer").css("visibility", "visible");
  }

  addNewQuestion(){
    this.questionService.addNewQuestion(this.idGroup, this.f.addQuestion.value, this.f.addType.value, this.f.addDifficulty.value,
      this.f.addPoints.value, this.f.addAnswerTime.value)
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

  editQuestion(question: Question){
    this.questionService.editQuestion(this.idGroup, question.id, this.f.addQuestion.value, this.f.addType.value, this.f.addDifficulty.value,
      this.f.addPoints.value, this.f.addAnswerTime.value)
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

  deleteQuestion(question: Question){
    this.questionService.deleteQuestion(this.idGroup, question.id)
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


  addNewAnswer(question: Question){
    this.questionService.addNewAnswer(this.idGroup, question.id, this.a.closedAnswer.value, this.a.correct.value)
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

  editAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.editAnswer(this.idGroup, question.id, closedAnswer.id, this.a.closedAnswer.value, this.a.correct.value)
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

  deleteAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.deleteAnswer(this.idGroup, question.id, closedAnswer.id)
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

  changeCorrectAnswer(question: Question, closedAnswer: ClosedAnswer){
    this.questionService.changeCorrectAnswer(this.idGroup, question.id, closedAnswer.id)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Zmieniono correct!");
      },
      error => {
        console.log("Nie mozna pobrac!");
        this.message = error["error"]["error"];
      });
  }


}
