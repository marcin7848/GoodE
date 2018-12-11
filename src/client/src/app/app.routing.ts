import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./component/home/home.component";
import {LoginComponent} from "./component/account/login/login.component";
import {RegisterComponent} from "./component/account/register/register.component";
import {ResendActivationCodeComponent} from "./component/account/resend-activation-code/resend-activation-code.component";
import {ActivateAccountComponent} from "./component/account/activate-account/activate-account.component";
import {ResetPasswordRequestComponent} from "./component/account/reset-password-request/reset-password-request.component";
import {ResetPasswordComponent} from "./component/account/reset-password/reset-password.component";
import {ChangeAccessRoleComponent} from "./component/account/change-access-role/change-access-role.component";
import {EditAccountComponent} from "./component/account/edit-account/edit-account.component";
import {GroupComponent} from "./component/group/group.component";

const routes: Routes = [
  {path : '', component : HomeComponent},
  {path : 'login', component : LoginComponent},
  {path : 'login/:expired', component : LoginComponent},
  {path : 'logout', component : LoginComponent},
  {path : 'register', component : RegisterComponent},
  {path : 'account/resendActivationCode', component : ResendActivationCodeComponent},
  {path : 'account/activate/:activationCode', component : ActivateAccountComponent},
  {path : 'account/sendResetPasswordRequest', component : ResetPasswordRequestComponent},
  {path : 'account/resetPassword/:resetPasswordCode', component : ResetPasswordComponent},
  {path : 'account/changeAccessRole', component : ChangeAccessRoleComponent},
  {path : 'account/edit', component : EditAccountComponent},
  {path : 'group', component : GroupComponent},
];

export const routing = RouterModule.forRoot(routes);
