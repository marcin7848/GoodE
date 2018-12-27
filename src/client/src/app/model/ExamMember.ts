import {Exam} from "./Exam";
import {ExamMemberQuestion} from "./ExamMemberQuestion";
import {Account} from "./Account";

export class ExamMember {
  id: number;
  blocked: boolean;
  causeOfBlockade: string;
  position: number;
  exam: Exam;
  account: Account;
  examMemberQuestions: ExamMemberQuestion[];
}
