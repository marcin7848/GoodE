import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AccountService } from './service/account/account.service';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';

import { MatButtonModule, MatCardModule, MatInputModule, MatListModule, MatToolbarModule } from '@angular/material';
import { HomeComponent } from './component/home/home.component';
import {routing} from "./app.routing";
import { LoginComponent } from './component/account/login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { CookieService } from 'ngx-cookie-service';
import {Interceptor} from "./app.interceptor";
import { RegisterComponent } from './component/account/register/register.component';
import { ResendActivationCodeComponent } from './component/account/resend-activation-code/resend-activation-code.component';
import { ActivateAccountComponent } from './component/account/activate-account/activate-account.component';
import { ResetPasswordRequestComponent } from './component/account/reset-password-request/reset-password-request.component';
import { ResetPasswordComponent } from './component/account/reset-password/reset-password.component';
import { ChangeAccessRoleComponent } from './component/account/change-access-role/change-access-role.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    ResendActivationCodeComponent,
    ActivateAccountComponent,
    ResetPasswordRequestComponent,
    ResetPasswordComponent,
    ChangeAccessRoleComponent
  ],
  imports: [
    routing,
    BrowserModule,
    HttpClientModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatListModule,
    MatToolbarModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    AccountService,
    CookieService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
