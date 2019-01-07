import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Account} from "../../../model/Account";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {
  editAccountForm: FormGroup;
  message = "";
  loggedAccount: Account;
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) { }

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
        location.href = '/';
      });
  }

  get f() { return this.editAccountForm.controls; }

  onSubmit(){
    this.submitted = true;
    if (this.editAccountForm.invalid) {
      return;
    }
    this.loading = true;

    this.accountService.editAccount(this.f.email.value, this.f.password.value,
      this.f.firstName.value, this.f.lastName.value, this.f.currentPassword.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = this.translateService.instant('accountEdited');
        setTimeout(function(){
          location.href = '/';
        }, 3000);
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

}
