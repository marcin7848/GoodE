import {Component, OnInit} from "@angular/core";
import {Router} from "@angular/router";
import {AccountService} from "../shared/service/account/account.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {first} from "rxjs/operators";
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private accountService: AccountService,
    private cookieService: CookieService) {}

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

    // stop here if form is invalid
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.accountService.getLoginToken(this.f.username.value, this.f.password.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log(data['access_token']);
        this.cookieService.set("Authorization", data['access_token']);
      },
      error => {
        this.error = "Niepoprawne dane!";
        this.loading = false;
      });
  }
}
