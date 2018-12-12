import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {GroupService} from "../../service/group/group.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Group} from "../../model/Group";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit {
  groupsForm: FormGroup;
  message = "";
  listOfMyGroups: Group[];
  listOfAllGroups: Group[];

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private groupService: GroupService) { }

  ngOnInit() {
    this.groupsForm = this.formBuilder.group({
      listOfMyGroups: [''],
      listOfAllGroups: ['']
    });

    this.groupService.getMyGroups()
    .pipe(first())
    .subscribe(
      data => {
        this.listOfMyGroups = data;
      },
      error => {
        console.log(error);
        console.log("Nie mozna pobrac!");
        this.message = error["error"]["error"];
      });

    this.groupService.getAllGroups()
    .pipe(first())
    .subscribe(
      data => {
        this.listOfAllGroups = data;
      },
      error => {
        console.log(error);
        console.log("Nie mozna pobrac!");
        this.message = error["error"]["error"];
      });

  }

  get f() { return this.groupsForm.controls; }

}
