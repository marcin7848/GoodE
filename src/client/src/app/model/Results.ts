import {Exam} from "./Exam";
import {ExamMemberQuestionResults} from "./ExamMemberQuestionResults";

export class Results {
  points: number;
  maxPoints: number;
  percentResult: number;
  idExamMember: number;
  exam: Exam;
  examMemberQuestionResults: ExamMemberQuestionResults[];
}
