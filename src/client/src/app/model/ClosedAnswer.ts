import {Question} from "./Question";

export class ClosedAnswer {
  id: number;
  question: Question;
  closedAnswer: string;
  correct: boolean;
}
