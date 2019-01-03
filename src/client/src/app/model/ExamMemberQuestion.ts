import {Exam} from "./Exam";
import {ExamMember} from "./ExamMember";
import {ExamQuestion} from "./ExamQuestion";
import {ExamAnswer} from "./ExamAnswer";

export class ExamMemberQuestion {
  id: number;
  position: number;
  examMember: ExamMember;
  examQuestion: ExamQuestion
  examAnswers: ExamAnswer[];
}
