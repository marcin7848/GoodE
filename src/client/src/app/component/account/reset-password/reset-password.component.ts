import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {ActivatedRoute} from "@angular/router";

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

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private route: ActivatedRoute) { }

  ngOnInit() {

    this.route.params.subscribe(params => {
      this.resetPasswordCode = params['resetPasswordCode'];
    });

    this.accountService.checkResetPasswordCode(this.resetPasswordCode)
    .pipe(first())
    .subscribe(
      data => {
        this.correctCode = true;
      },
      error => {
        this.message = error["error"]["error"];
      });

    this.resetPasswordForm = this.formBuilder.group({
      password: ['', Validators.required]
    });
  }

  get f() { return this.resetPasswordForm.controls; }

  onSubmit(){
    this.accountService.resetPassword(this.resetPasswordCode, this.f.password.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = "Hasło zostało zresetowane!";
      },
      error => {
        this.message = error["error"]["error"];
      });
  }

}
