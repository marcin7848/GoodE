import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  message = "";
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) {
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
        username: ['', Validators.required],
        email: ['', Validators.required],
        password: ['', Validators.required],
        register_no: ['', Validators.required],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        confirmPassword: ['', Validators.required]
      },
      {validator: this.passwordValidator()});
  }

  get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.registerForm.invalid) {
      return;
    }
    this.loading = true;

    this.accountService.register(this.f.username.value, this.f.email.value, this.f.password.value, this.f.register_no.value,
      this.f.firstName.value, this.f.lastName.value)
    .pipe(first())
    .subscribe(
      data => {
        this.loading = false;
        this.message = this.translateService.instant('registered');
        setTimeout(function () {
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

  passwordValidator(): ValidatorFn {
    return (group: FormGroup): ValidationErrors => {
      const password = group.controls['password'];
      const confirmPassword = group.controls['confirmPassword'];
      if (password.value !== confirmPassword.value && confirmPassword.errors == null) {
        confirmPassword.setErrors({ notEquivalent: true });
      }
      return;
    };
  }

}
