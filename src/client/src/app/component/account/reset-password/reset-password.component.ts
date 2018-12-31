import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {ActivatedRoute} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  message = "";
  correctCode = false;
  resetPasswordCode = "";
  loading = false;
  submitted = false;


  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private route: ActivatedRoute,
              private translateService: TranslateService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.resetPasswordCode = params['resetPasswordCode'];

      this.accountService.checkResetPasswordCode(this.resetPasswordCode)
      .pipe(first())
      .subscribe(
        data => {
          this.correctCode = true;
        },
        error => {
          location.href = '/';
        });
    });

    this.resetPasswordForm = this.formBuilder.group({
      password: ['', Validators.required]
    });
  }

  get f() { return this.resetPasswordForm.controls; }

  onSubmit(){
    this.submitted = true;

    if (this.resetPasswordForm.invalid) {
      return;
    }

    this.loading = true;

    this.accountService.resetPassword(this.resetPasswordCode, this.f.password.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = this.translateService.instant('resetPasswordDone');
        setTimeout(function(){
          location.href = '/login';
        }, 5000);
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      });
  }

}
