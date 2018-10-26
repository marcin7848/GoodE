import {Account} from "./Account";

export class ActivationCode {
  id: number;
  account: Account;
  type: number;
  code: string;
  creationTime: string;
}
