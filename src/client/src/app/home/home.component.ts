import { Component, OnInit } from '@angular/core';

import {Account} from "../shared/model/Account";
import {AccountService} from "../shared/service/account/account.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  loggedAccount: Account;

  constructor(private router: Router, private accountService: AccountService) { }

  ngOnInit() {
    this.accountService.getLoggedAccount().
    subscribe(data => {
      this.loggedAccount = data;
    })
  }

}