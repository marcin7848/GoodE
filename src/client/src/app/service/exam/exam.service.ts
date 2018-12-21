import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";

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

}
