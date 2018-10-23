import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from "rxjs";

import {Account} from "../../model/Account";

@Injectable()
export class AccountService {
  constructor(private http: HttpClient) {
  }

  private baseUri = '//localhost:8081/account';

  public getLoggedAccount(){
    return this.http.get<Account>(this.baseUri + "/getLoggedAccount")
  }

}
