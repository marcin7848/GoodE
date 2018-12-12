import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Account} from "../../../model/Account";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Group} from "../../../model/Group";
import {GroupService} from "../../../service/group/group.service";

@Component({
  selector: 'app-add-new-group',
  templateUrl: './add-new-group.component.html',
  styleUrls: ['./add-new-group.component.scss']
})
export class AddNewGroupComponent implements OnInit {

  addNewGroupForm: FormGroup;
  message = "";
  listOfGroups: Group[];

  constructor(private formBuilder: FormBuilder,
              private groupService: GroupService) { }


  ngOnInit() {
    this.addNewGroupForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      password: [''],
      possibleToJoin: ['', Validators.required],
      acceptance: ['', Validators.required],
      hidden: ['', Validators.required],
      idGroupParent: ['']
    });

    this.groupService.getMyGroups().
    subscribe(data => {
        this.listOfGroups = data;
      },
      error => {
        console.log("Nie mozna pobrac!");
      });
  }

  get f() { return this.addNewGroupForm.controls; }

  onSubmit(){
    this.groupService.addNewGroup(this.f.name.value, this.f.description.value,
      this.f.password.value, this.f.possibleToJoin.value, this.f.acceptance.value,
      this.f.hidden.value, this.f.idGroupParent.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Grupa dodana");
      },
      error => {
        console.log(error);
        console.log("Niepoprawne dane!");
        this.message = error["error"]["error"];
      });
  }
}
