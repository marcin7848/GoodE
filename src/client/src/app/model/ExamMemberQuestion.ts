import {Exam} from "./Exam";
import {ExamMember} from "./ExamMember";
import {ExamQuestion} from "./ExamQuestion";

export class ExamMemberQuestion {
  id: number;
  position: number;
  examMember: ExamMember;
  examQuestion: ExamQuestion;
}
