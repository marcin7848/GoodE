import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  constructor(private http: HttpClient) { }

  private baseUri = '//localhost:8081/group';

  public getQuestions(idGroup: number){
    return this.http.get<any>(this.baseUri + "/"+idGroup+"/question/getAll");
  }

  public addNewQuestion(idGroup: number, questionV: string, typeV: number, difficultyV: number, pointsV: number, answerTimeV: number){
    const body = {question: questionV, type: typeV, difficulty: difficultyV, points: pointsV, answerTime: answerTimeV};
    return this.http.post<any>(this.baseUri + "/"+idGroup+"/question/addNew", body);
  }

  public editQuestion(idGroup: number, idQuestion: number, questionV: string, typeV: number, difficultyV: number, pointsV: number, answerTimeV: number){
    const body = {question: questionV, type: typeV, difficulty: difficultyV, points: pointsV, answerTime: answerTimeV};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/edit", body);
  }

  public deleteQuestion(idGroup: number, idQuestion: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/delete", body);
  }

  public addNewAnswer(idGroup: number, idQuestion: number, closedAnswerV: string, correctV: boolean){
    const body = {closedAnswer: closedAnswerV, correct: correctV};
    return this.http.post<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/closedAnswer/addNew", body);
  }

  public editAnswer(idGroup: number, idQuestion: number, idClosedAnswer: number, closedAnswerV: string, correctV: boolean){
    const body = {closedAnswer: closedAnswerV, correct: correctV};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/closedAnswer/"+idClosedAnswer+"/edit", body);
  }

  public deleteAnswer(idGroup: number, idQuestion: number, idClosedAnswer: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/closedAnswer/"+idClosedAnswer+"/delete", body);
  }

  public changeCorrectAnswer(idGroup: number, idQuestion: number, idClosedAnswer: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/question/"+idQuestion+"/closedAnswer/"+idClosedAnswer+"/changeCorrect", body);
  }
}
