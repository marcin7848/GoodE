import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {ActivatedRoute} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

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
              private translateService: TranslateService,
              private snackBar: MatSnackBar) {
  }

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
        password: ['', Validators.required],
        confirmPassword: ['', Validators.required]
      },
      {validator: this.passwordValidator()});
  }

  get f() {
    return this.resetPasswordForm.controls;
  }

  onSubmit() {
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
        setTimeout(function () {
          location.href = '/login';
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

  passwordValidator(): ValidatorFn {
    return (group: FormGroup): ValidationErrors => {
      const password = group.controls['password'];
      const confirmPassword = group.controls['confirmPassword'];
      if (password.value !== confirmPassword.value && confirmPassword.errors == null) {
        confirmPassword.setErrors({notEquivalent: true});
      }
      return;
    };
  }

}
