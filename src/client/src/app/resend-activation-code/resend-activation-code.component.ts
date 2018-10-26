import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../shared/service/account/account.service";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-resend-activation-code',
  templateUrl: './resend-activation-code.component.html',
  styleUrls: ['./resend-activation-code.component.scss']
})
export class ResendActivationCodeComponent implements OnInit {
  resendActivationCodeForm: FormGroup;
  message = "";

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService) { }

  ngOnInit() {
    this.resendActivationCodeForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  get f() { return this.resendActivationCodeForm.controls; }

  onSubmit(){
    this.accountService.resendActivationCode(this.f.email.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = "Kod został wysłany!";
        console.log("Kod został wysłany");
      },
      error => {
        console.log(error);
        this.message = error["error"]["error"];
      });
  }

}
