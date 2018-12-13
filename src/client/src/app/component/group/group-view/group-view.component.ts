import { Component, OnInit } from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {GroupService} from "../../../service/group/group.service";
import {first} from "rxjs/operators";
import {Group} from "../../../model/Group";
import {GroupMember} from "../../../model/GroupMember";
import {AccountService} from "../../../service/account/account.service";

@Component({
  selector: 'app-group-view',
  templateUrl: './group-view.component.html',
  styleUrls: ['./group-view.component.scss']
})
export class GroupViewComponent implements OnInit {
  message = "";
  id: number;
  group: Group;
  groupMembers: GroupMember[];
  actionMessage = "";

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private route: ActivatedRoute,
              private groupService: GroupService,
              private accountService: AccountService) { }

  ngOnInit() {

    this.route.params.subscribe(params => {
      this.id = params['id'];

      this.groupService.getGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.group = data;
        },
        error => {
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });

      this.groupService.getGroupMembers(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.groupMembers = data;
        },
        error => {
          console.log(error);
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });
    });
  }

  joinToGroup(idGroup: number){
    this.groupService.joinToTheGroup(idGroup).
    subscribe(data => {
        this.actionMessage = "Dolaczyles do grupy!";
      },
      error => {
        this.actionMessage = error["error"]["error"];
        console.log("Nie mozna pobrac!");
      })
  }

  acceptNewMember(idGroup: number, idGroupMember: number){
    this.groupService.acceptMember(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Zaakceptowano!");
      },
      error => {
        console.log("Nie mozna pobrac!");
      })
  }

  leaveGroup(idGroup: number){
    this.groupService.leaveGroup(idGroup).
    subscribe(data => {
        console.log("Opusciles grupe!");
      },
      error => {
        console.log("Nie mozna pobrac!");
      })
  }

  deleteGroupMember(idGroup: number, idGroupMember: number){
    this.groupService.deleteGroupMember(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Usunieto czlonka grupy!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  promoteToTeacher(idGroup: number, idGroupMember: number){
    this.groupService.promoteToTeacher(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Pomyslnie nadano range nauczyciela!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  reduceToStudent(idGroup: number, idGroupMember: number){
    this.groupService.reduceToStudent(idGroup, idGroupMember).
    subscribe(data => {
        console.log("Pomyslnie zmieniono range na Student!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

  deleteGroup(idGroup: number){
    this.groupService.deleteGroup(idGroup).
    subscribe(data => {
        console.log("Pomyslnie usunieto grupe!");
      },
      error => {
        console.log("Nie mozna wykonac!");
      })
  }

}
