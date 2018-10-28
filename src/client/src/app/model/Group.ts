import {GroupMember} from "./GroupMember";

export class Group{
  id: number;
  name: string;
  description: string;
  password: string;
  can_join: boolean;
  need_acceptance: boolean;
  is_show: boolean;
  id_group_parent: number;
  order: number;
  creationTime: string;
  groupMembers: GroupMember[];

}
