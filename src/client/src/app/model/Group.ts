import {GroupMember} from "./GroupMember";
import {QuestionGroup} from "./QuestionGroup";

export class Group{
  id: number;
  name: string;
  description: string;
  password: string;
  possibleToJoin: boolean;
  acceptance: boolean;
  hidden: boolean;
  idGroupParent: number;
  position: number;
  creationTime: string;
  groupMembers: GroupMember[];

}
