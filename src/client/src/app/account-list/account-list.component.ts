import { Component, OnInit } from '@angular/core';
import { AccountService } from '../shared/account/account.service';

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})

export class AccountListComponent implements OnInit {
  accounts: Array<any>;
  constructor(private accountService: AccountService) { }
  ngOnInit() {
    this.accountService.getAll().subscribe(data => {
      this.accounts = data;
    });
  }
}
