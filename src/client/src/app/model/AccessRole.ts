import {Account} from "./Account";
import {GroupMember} from "./GroupMember";

export class AccessRole {
  id: number;
  role: string;
  accounts: Account[];
  groupMembers: GroupMember[];

}
