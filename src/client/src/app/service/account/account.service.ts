import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Account} from "../../model/Account";
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AccountService {
  constructor(private http: HttpClient,
              private cookieService: CookieService) {
  }

  private baseUri = '//localhost:8081/account';
  private tokenUri = '//localhost:8081/oauth/token';

  public getLoginToken(username: string, password: string) {
    let headers = new HttpHeaders().set('Authorization', 'Basic ' + btoa("goodEApp-client:goodEApp-secret"));
    headers = headers.set('Content-Type', 'application/x-www-form-urlencoded');

    let body = "username=" + username + "&password=" + password + "&grant_type=password";

    return this.http.post<any>(this.tokenUri, body, { headers: headers });
  }


  public getLoggedAccount(){
    return this.http.get<Account>(this.baseUri + "/getLoggedAccount" )
  }

  public register(usernameV: string, emailV: string, passwordV: string, register_noV: number, firstNameV: string, lastNameV: string){
    const body = {username: usernameV, email: emailV, password: passwordV, register_no: register_noV, firstName: firstNameV, lastName: lastNameV};
    return this.http.post<any>(this.baseUri + "/register", body);
  }

  public resendActivationCode(emailV: string){
    const body = {email: emailV};
    return this.http.post<any>(this.baseUri + "/resendActivationCode", body);
  }

  public activateAccount(activationCode: string){
    return this.http.get<any>(this.baseUri + "/activate/" + activationCode);
  }

  public sendResetPasswordRequest(emailV: string){
    const body = {email: emailV};
    return this.http.post<any>(this.baseUri + "/sendResetPasswordRequest", body);
  }

  public checkResetPasswordCode(resetPasswordCode: string){
    return this.http.get<any>(this.baseUri + "/resetPassword/" + resetPasswordCode);
  }

  public resetPassword(resetPasswordCode: string, passwordV: string){
    const body = {password: passwordV};
    return this.http.post<any>(this.baseUri + "/resetPassword/" + resetPasswordCode, body);
  }

  public getAllAccessRole(){
    return this.http.get<any>(this.baseUri + "/getAllAccessRole");
  }

  public changeAccessRole(usernameV: string, accessRoleV: string){
    const body = {accessRole: accessRoleV};
    return this.http.post<any>(this.baseUri + "/" + usernameV + "/changeAccessRole", body);
  }

  public editAccount(emailV: string, passwordV: string, firstNameV: string, lastNameV: string, currentPasswordV: string){
    const body = {email: emailV, password: passwordV, firstName: firstNameV, lastName: lastNameV, currentPassword: currentPasswordV};
    return this.http.patch<any>(this.baseUri + "/edit", body);
  }

}
