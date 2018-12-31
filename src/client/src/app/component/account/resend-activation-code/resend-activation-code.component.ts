import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-resend-activation-code',
  templateUrl: './resend-activation-code.component.html',
  styleUrls: ['./resend-activation-code.component.scss']
})
export class ResendActivationCodeComponent implements OnInit {
  resendActivationCodeForm: FormGroup;
  message = "";
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private translateService: TranslateService) { }

  ngOnInit() {
    this.resendActivationCodeForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  get f() { return this.resendActivationCodeForm.controls; }

  onSubmit(){
    this.submitted = true;

    if (this.resendActivationCodeForm.invalid) {
      return;
    }

    this.loading = true;

    this.accountService.resendActivationCode(this.f.email.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = this.translateService.instant('activationCodeSent');
        setTimeout(function(){
          location.href = '/';
        }, 5000);
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      });
  }

}
