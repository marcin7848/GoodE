import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-reset-password-request',
  templateUrl: './reset-password-request.component.html',
  styleUrls: ['./reset-password-request.component.scss']
})
export class ResetPasswordRequestComponent implements OnInit {
  sendResetPasswordRequestForm: FormGroup;
  message = "";
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) { }

  ngOnInit() {
    this.sendResetPasswordRequestForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  get f() { return this.sendResetPasswordRequestForm.controls; }

  onSubmit(){
    this.submitted = true;

    if (this.sendResetPasswordRequestForm.invalid) {
      return;
    }

    this.loading = true;


    this.accountService.sendResetPasswordRequest(this.f.email.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = this.translateService.instant('resetPasswordCodeSent');
        setTimeout(function(){
          location.href = '/';
        }, 5000);
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
