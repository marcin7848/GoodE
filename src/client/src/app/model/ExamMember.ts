import {Exam} from "./Exam";

export class ExamMember {
  id: number;
  blocked: boolean;
  causeOfBlockade: string;
  position: number;
  exam: Exam;
}
