import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {GroupService} from "../../service/group/group.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit {
  groupsForm: FormGroup;

  constructor(private formBuilder: FormBuilder,
              private router: Router,
              private groupService: GroupService) { }

  ngOnInit() {
    this.groupsForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      password: [''],
      possibleToJoin: ['', Validators.required],
      acceptance: ['', Validators.required],
      hidden: ['', Validators.required],
      idGroupParent: ['']
    });

  }

}
