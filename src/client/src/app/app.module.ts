import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppComponent } from './app.component';
import { AccountService } from './shared/account/account.service';
import { HttpClientModule } from '@angular/common/http';
import {AccountListComponent} from "./account-list/account-list.component";

import { MatButtonModule, MatCardModule, MatInputModule, MatListModule, MatToolbarModule } from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    AccountListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatListModule,
    MatToolbarModule
  ],
  providers: [AccountService],
  bootstrap: [AppComponent]
})
export class AppModule { }
