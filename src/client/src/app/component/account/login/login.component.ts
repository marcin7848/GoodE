import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {AccountService} from "../../../service/account/account.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {first} from "rxjs/operators";
import { CookieService } from 'ngx-cookie-service';
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  message = '';
  expired = false;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private accountService: AccountService,
    private cookieService: CookieService,
    private translateService: TranslateService,
    private snackBar: MatSnackBar) {}

  ngOnInit() {
    if(this.router.url === '/logout'){
      this.cookieService.delete("Authorization");
      location.href = '/';
      return;
    }

    this.route.params.subscribe(params => {
      this.expired = !!params['expired'];
      if(this.expired){
        this.cookieService.delete("Authorization");
      }
    });

    if(this.cookieService.get("Authorization")){
      this.router.navigate(['/']);
    }

    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.accountService.getLoginToken(this.f.username.value, this.f.password.value)
    .pipe(first())
    .subscribe(
      data => {
        this.cookieService.set("Authorization", data['access_token']);
        location.href = '/';
      },
      error => {
        this.message = this.translateService.instant('incorrectData');
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
        this.loading = false;
      });
  }
}
