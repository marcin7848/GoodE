import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {ExamAnswerWrapper} from "../../model/ExamAnswerWrapper";

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  constructor(private http: HttpClient,
              private cookieService: CookieService) { }

  private baseUri = '//localhost:8081/exam';
  private tokenUri = '//localhost:8081/oauth/token';

  public getAllExamsByIdGroup(idGroup: number){
    return this.http.get<any>(this.baseUri + "/getAll/group/"+idGroup);
  }

  public getExamFullById(idExam: number){
    return this.http.get<any>(this.baseUri + "/"+idExam+"/get");
  }

  public getRunningExamManagement(idExam: number){
    return this.http.get<any>(this.baseUri + "/"+idExam+"/get/runningManagement");
  }

  public getRunningExam(idExam: number){
    return this.http.get<any>(this.baseUri + "/"+idExam+"/get/running");
  }

  public addNewExam(idGroupV: number, titleV: string, typeV: number, difficultyV: number, showAllQuestionsV: boolean, returnToQuestionsV: boolean, sendResultsInstantlyV: boolean,
                    showFullResultsV: boolean, mixQuestionsV: boolean, percentToPassV: number, numberOfQuestionsV: number, maxTimeV: number){
    const body = {title: titleV, type: typeV, difficulty: difficultyV, showAllQuestions: showAllQuestionsV, returnToQuestions: returnToQuestionsV,
      sendResultsInstantly: sendResultsInstantlyV, showFullResults: showFullResultsV, mixQuestions: mixQuestionsV, percentToPass: percentToPassV,
      numberOfQuestions: numberOfQuestionsV, maxTime: maxTimeV};
    return this.http.post<any>(this.baseUri + "/addNew/group/"+idGroupV, body);
  }

  public editExam(idExam: number, titleV: string, typeV: number, difficultyV: number, showAllQuestionsV: boolean, returnToQuestionsV: boolean, sendResultsInstantlyV: boolean,
                    showFullResultsV: boolean, mixQuestionsV: boolean, percentToPassV: number, numberOfQuestionsV: number, maxTimeV: number){
    const body = {title: titleV, type: typeV, difficulty: difficultyV, showAllQuestions: showAllQuestionsV, returnToQuestions: returnToQuestionsV,
      sendResultsInstantly: sendResultsInstantlyV, showFullResults: showFullResultsV, mixQuestions: mixQuestionsV, percentToPass: percentToPassV,
      numberOfQuestions: numberOfQuestionsV, maxTime: maxTimeV};
    return this.http.patch<any>(this.baseUri + "/"+idExam+"/edit/", body);
  }

  public addAllExamQuestions(idExam: number){
    const body = {};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/examQuestion/addAll", body);
  }

  public addQuestionToExam(idExam: number, idQuestion: number){
    const body = {};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/examQuestion/add/"+idQuestion, body);
  }

  public deleteExamQuestion(idExam: number, idExamQuestion: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idExam+"/examQuestion/"+idExamQuestion+"/delete", body);
  }

  public changeExamQuestionPosition(idExam: number, idExamQuestion: number, position: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idExam+"/examQuestion/"+idExamQuestion+"/changePosition/"+position, body);
  }

  public initiateJoingToExam(idExam: number, passwordV: string, colorV: string){
    const body = {password: passwordV, color: colorV};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/initiateExam", body);
  }

  public startExam(idExam: number, finishTimeV: string){
    const body = {finishTime: finishTimeV};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/start", body);
  }

  public joinToRunningExam(idExam: number, passwordV: string){
    const body = {password: passwordV};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/join", body);
  }

  public blockExamMember(idExam: number, idExamMember: number, causeOfBlockadeV: string){
    const body = {causeOfBlockade: causeOfBlockadeV};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/examMember/"+idExamMember+"/block", body);
  }

  public addAnswers(idExam: number, examAnswerWrapper: ExamAnswerWrapper[]){
    return this.http.post<any>(this.baseUri + "/"+idExam+"/addAnswer", examAnswerWrapper);
  }

  public changeExamMemberPosition(idExam: number, position: number){
    const body = {};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/changeExamMemberPosition/"+position, body);
  }

  public finishExam(idExam: number){
    const body = {};
    return this.http.post<any>(this.baseUri + "/"+idExam+"/finish", body);
  }

  public changeCorrectExamClosedAnswer(idExam: number, idExamQuestion: number, idExamClosedAnswer: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idExam+"/examQuestion/"+idExamQuestion+"/examClosedAnswer/"+idExamClosedAnswer+"/changeCorrect", body);
  }

}
