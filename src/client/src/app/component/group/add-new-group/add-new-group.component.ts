import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Account} from "../../../model/Account";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-add-new-group',
  templateUrl: './add-new-group.component.html',
  styleUrls: ['./add-new-group.component.scss']
})
export class AddNewGroupComponent implements OnInit {

  addNewGroupForm: FormGroup;
  message = "";
  loggedAccount: Account;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService) { }


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
  }

  get f() { return this.addNewGroupForm.controls; }

  onSubmit(){

  }
}
