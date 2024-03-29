import {Group} from "./Group";
import {ExamQuestion} from "./ExamQuestion";
import {ExamMember} from "./ExamMember";

export class Exam {
  id: number;
  title: string;
  password: string;
  color: string;
  type: number;
  difficulty: number;
  showAllQuestions: boolean;
  returnToQuestions: boolean;
  sendResultsInstantly: boolean;
  showFullResults: boolean;
  mixQuestions: boolean;
  maxTime: number;
  additionalTime: number;
  numberOfQuestions: number;
  joining: boolean;
  started: boolean;
  finished: boolean;
  rated: boolean;
  startTime: string;
  finishTime: string;
  creationTime: string;
  percentToPass: number;
  draft: boolean;
  group: Group;
  examQuestions: ExamQuestion[];
  examMembers: ExamMember[];
}
