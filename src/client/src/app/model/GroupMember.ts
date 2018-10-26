import {Account} from "./Account";
import {Group} from "./Group";
import {AccessRole} from "./AccessRole";

export class GroupMember{
  id: number;
  account: Account;
  group: Group;
  accessRole: AccessRole;
  accepted: boolean;

}
