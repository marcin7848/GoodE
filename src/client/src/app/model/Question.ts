import {ClosedAnswer} from "./ClosedAnswer";
import {QuestionGroup} from "./QuestionGroup";

export class Question {

  id: number;
  question: string;
  type: number;
  difficulty: number;
  points: number;
  answerTime: number;
  questionGroups: QuestionGroup[];
  closedAnswers: ClosedAnswer[];
}
