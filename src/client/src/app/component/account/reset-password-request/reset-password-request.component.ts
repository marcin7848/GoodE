import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-reset-password-request',
  templateUrl: './reset-password-request.component.html',
  styleUrls: ['./reset-password-request.component.scss']
})
export class ResetPasswordRequestComponent implements OnInit {
  sendResetPasswordRequestForm: FormGroup;
  message = "";

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService) { }

  ngOnInit() {
    this.sendResetPasswordRequestForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  get f() { return this.sendResetPasswordRequestForm.controls; }

  onSubmit(){
    this.accountService.sendResetPasswordRequest(this.f.email.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = "Kod resetujacy haslo został wysłany!";
      },
      error => {
        this.message = error["error"]["error"];
      });
  }
}
