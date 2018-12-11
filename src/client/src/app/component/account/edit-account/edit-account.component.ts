import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Account} from "../../../model/Account";

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {
  editAccountForm: FormGroup;
  message = "";
  loggedAccount: Account;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService) { }

  ngOnInit() {
    this.editAccountForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: [''],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      currentPassword: ['', Validators.required]
    });

    this.accountService.getLoggedAccount().
    subscribe(data => {
        this.loggedAccount = data;
        this.f.email.setValue(this.loggedAccount.email);
        this.f.firstName.setValue(this.loggedAccount.firstName);
        this.f.lastName.setValue(this.loggedAccount.lastName);
      },
      error => {
        console.log("Nie mozna pobrac!");
      });


  }

  get f() { return this.editAccountForm.controls; }

  onSubmit(){
    this.accountService.editAccount(this.f.email.value, this.f.password.value,
      this.f.firstName.value, this.f.lastName.value, this.f.currentPassword.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Konto wyedytowane!!!");
      },
      error => {
        console.log(error);
        console.log("Niepoprawne dane!");
        this.message = error["error"]["error"];
      });
  }

}
