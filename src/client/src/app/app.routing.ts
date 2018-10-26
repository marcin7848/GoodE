import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./component/home/home.component";
import {LoginComponent} from "./component/account/login/login.component";
import {RegisterComponent} from "./component/account/register/register.component";
import {ResendActivationCodeComponent} from "./component/account/resend-activation-code/resend-activation-code.component";
import {ActivateAccountComponent} from "./component/account/activate-account/activate-account.component";

const routes: Routes = [
  {path : '', component : HomeComponent},
  {path : 'login', component : LoginComponent},
  {path : 'login/:expired', component : LoginComponent},
  {path : 'logout', component : LoginComponent},
  {path : 'register', component : RegisterComponent},
  {path : 'account/resendActivationCode', component : ResendActivationCodeComponent},
  {path : 'account/activate/:activationCode', component : ActivateAccountComponent},
];

export const routing = RouterModule.forRoot(routes);
