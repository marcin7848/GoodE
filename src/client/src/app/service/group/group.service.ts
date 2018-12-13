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
                     idGroupParentV: number){
    const body = {name: nameV, description: descriptionV, password: passwordV, possibleToJoin: possibleToJoinV, acceptance: acceptanceV, hidden: hiddenV,
      idGroupParent: idGroupParentV};
    return this.http.post<any>(this.baseUri + "/addNew", body);
  }

  public getMyGroups(){
    return this.http.get<any>(this.baseUri + "/getMyGroups");
  }

  public getAllGroups(){
    return this.http.get<any>(this.baseUri + "/getAllGroups");
  }

  public getGroup(id: number){
    return this.http.get<any>(this.baseUri + "/"+id+"/view");
  }

  public getGroupMembers(id: number){
    return this.http.get<any>(this.baseUri + "/"+id+"/getGroupMembers");
  }

  public joinToTheGroup(idGroup: number){
    const body = {};
    return this.http.post<any>(this.baseUri + "/"+idGroup+"/join", body);
  }

  public acceptMember(idGroup: number, idGroupMember: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/member/"+idGroupMember+"/accept", body);
  }

  public leaveGroup(idGroup: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idGroup+"/leave", body);
  }

  public deleteGroupMember(idGroup: number, idGroupMember: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idGroup+"/deleteGroupMember/"+idGroupMember, body);
  }

  public promoteToTeacher(idGroup: number, idGroupMember: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/member/"+idGroupMember+"/changeAccessRole/ROLE_TEACHER", body);
  }

  public reduceToStudent(idGroup: number, idGroupMember: number){
    const body = {};
    return this.http.patch<any>(this.baseUri + "/"+idGroup+"/member/"+idGroupMember+"/changeAccessRole/ROLE_STUDENT", body);
  }

  public deleteGroup(idGroup: number){
    const body = {};
    return this.http.delete<any>(this.baseUri + "/"+idGroup+"/delete", body);
  }
}
