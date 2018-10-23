import {AccessRole} from "./AccessRole";
import {ActivationCode} from "./ActivationCode";
import {GroupMember} from "./GroupMember";

export class Account {

  id: number;
  username: string;
  email: string;
  password: string;
  register_no: number;
  accessRole: AccessRole;
  enabled: boolean;
  firstName: string;
  lastName: string;
  creationTime: string;
  activationCode: ActivationCode[];
  groupMember: GroupMember[];
}
