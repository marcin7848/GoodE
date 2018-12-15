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
import { EditAccountComponent } from './component/account/edit-account/edit-account.component';
import {GroupService} from "./service/group/group.service";
import { GroupComponent } from './component/group/group.component';
import { AddNewGroupComponent } from './component/group/add-new-group/add-new-group.component';
import { GroupViewComponent } from './component/group/group-view/group-view.component';
import { GroupEditComponent } from './component/group/group-edit/group-edit.component';
import {QuestionService} from "./service/question/question.service";
import { QuestionComponent } from './component/question/question.component';

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
    ChangeAccessRoleComponent,
    EditAccountComponent,
    GroupComponent,
    AddNewGroupComponent,
    GroupViewComponent,
    GroupEditComponent,
    QuestionComponent
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
    GroupService,
    QuestionService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }
