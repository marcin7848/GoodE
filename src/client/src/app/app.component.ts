import { Component } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {CookieService} from "ngx-cookie-service";
import {Account} from "./model/Account";
import {AccountService} from "./service/account/account.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'client';
  selected = 'pl';
  loggedAccount: Account;

  constructor(private translate: TranslateService,
              private cookieService: CookieService,
              private accountService: AccountService) {
    let language = this.cookieService.get("Language") ? this.cookieService.get("Language") : 'pl';
    translate.setDefaultLang(language);
    this.selected = language;
    this.accountService.getLoggedAccount().
    subscribe(data => {
        this.loggedAccount = data;
      },
      error => {
      });
  }

  changeLanguage(language: string){
    this.cookieService.set('Language', language);
    location.reload();
  }


}
