import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";

@Injectable({
  providedIn: 'root'
})
export class GroupService {

  constructor(private http: HttpClient,
              private cookieService: CookieService) { }

  private baseUri = '//localhost:8081/group';
  private tokenUri = '//localhost:8081/oauth/token';

  public addNewGroup(nameV: string, descriptionV: string, passwordV: string, possibleToJoinV: boolean, acceptanceV: boolean, hiddenV: boolean,
                     idGroupParentV: number, positionV: number){
    const body = {name: nameV, description: descriptionV, password: passwordV, possibleToJoin: possibleToJoinV, acceptance: acceptanceV, hidden: hiddenV,
      idGroupParent: idGroupParentV, position: positionV};
    return this.http.post<any>(this.baseUri + "/addNew", body);
  }
}