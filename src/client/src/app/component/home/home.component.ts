import { Component, OnInit } from '@angular/core';

import {Account} from "../../model/Account";
import {AccountService} from "../../service/account/account.service";
import {Router} from "@angular/router";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  loggedAccount: Account;

  constructor(private router: Router, private accountService: AccountService) {}

  ngOnInit() {
    this.accountService.getLoggedAccount().
    subscribe(data => {
      this.loggedAccount = data;
      if(this.loggedAccount.id == 0){
        this.router.navigate(['/login']);
      }
      else{
        this.router.navigate(['/group']);
      }
    },
      error => {
        this.router.navigate(['/login']);
      })
  }

}
