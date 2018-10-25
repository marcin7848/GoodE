import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Account} from "../../model/Account";

@Injectable()
export class AccountService {
  constructor(private http: HttpClient) {
  }

  private baseUri = '//localhost:8081/account';
  private tokenUri = '//localhost:8081/oauth/token';

  public getLoginToken(usernameCli: string, passwordCli: string) {
    let headers = new HttpHeaders().set('Content-Type', 'application/x-www-form-urlencoded');
    headers = headers.set('Authorization', 'Basic ' + btoa("goodEApp-client:goodEApp-secret"));

    let body = "username=" + usernameCli + "&password=" + passwordCli + "&grant_type=password";

    return this.http.post<any>(this.tokenUri, body, { headers: headers });
  }


  public getLoggedAccount(){
    return this.http.get<Account>(this.baseUri + "/getLoggedAccount")
  }

}
