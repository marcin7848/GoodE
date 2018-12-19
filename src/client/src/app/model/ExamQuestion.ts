import {Group} from "./Group";
import {Exam} from "./Exam";
import {ExamClosedAnswer} from "./ExamClosedAnswer";

export class ExamQuestion {
  id: number;
  question: string;
  type: number;
  difficulty: number;
  points: number;
  answerTimer: number;
  position: number;
  exam: Exam;
  examClosedAnswers: ExamClosedAnswer[];
}
