import {ExamMemberQuestion} from "./ExamMemberQuestion";
import {ExamClosedAnswer} from "./ExamClosedAnswer";

export class ExamAnswer {
  id: number;
  examMemberQuestion: ExamMemberQuestion;
  examClosedAnswer: ExamClosedAnswer;
}
