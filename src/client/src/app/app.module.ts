import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AccountService } from './service/account/account.service';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';

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
import {ExamService} from "./service/exam/exam.service";
import { ExamManagementComponent } from './component/exam/exam-management/exam-management.component';
import {RunningExamManagementComponent} from './component/exam/running-exam-management/running-exam-management.component';
import { RunningExamComponent } from './component/exam/running-exam/running-exam.component';
import { ExamResultsComponent } from './component/exam/exam-results/exam-results.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';

import {MatSelectModule} from '@angular/material/select';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatTabsModule} from '@angular/material/tabs';
import {MatIconModule} from '@angular/material/icon';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTableModule} from '@angular/material/table';
import {MatDividerModule} from '@angular/material/divider';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatDialogModule} from '@angular/material/dialog';
import { DialogBlockExamMemberComponent } from './component/exam/running-exam-management/dialog-block-exam-member/dialog-block-exam-member.component';
import { DialogGroupDeleteComponent } from './component/group/group-view/dialog-group-delete/dialog-group-delete.component';

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
    QuestionComponent,
    ExamManagementComponent,
    RunningExamManagementComponent,
    RunningExamComponent,
    ExamResultsComponent,
    DialogBlockExamMemberComponent,
    DialogGroupDeleteComponent
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
    ReactiveFormsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    MatSelectModule,
    BrowserAnimationsModule,
    MatTabsModule,
    MatIconModule,
    MatSlideToggleModule,
    MatTableModule,
    MatDividerModule,
    MatSnackBarModule,
    MatDialogModule
  ],
  providers: [
    AccountService,
    CookieService,
    GroupService,
    QuestionService,
    ExamService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true
    }],
  bootstrap: [AppComponent],
  entryComponents:[DialogBlockExamMemberComponent, DialogGroupDeleteComponent]
})
export class AppModule { }

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
