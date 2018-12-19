import {Group} from "./Group";
import {Exam} from "./Exam";

export class ExamQuestion {
  id: number;
  question: string;
  type: number;
  difficulty: number;
  points: number;
  answerTimer: number;
  position: number;
  exam: Exam;
}
