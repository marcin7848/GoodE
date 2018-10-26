import { Component, OnInit } from '@angular/core';
import {AccountService} from "../shared/service/account/account.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent implements OnInit {

  activationCode = "";
  message = "";

  constructor(private accountService: AccountService,
              private route: ActivatedRoute,) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.activationCode = params['activationCode'];
    });

    this.accountService.activateAccount(this.activationCode)
    .pipe(first())
    .subscribe(
      data => {
        this.message = "Konto zostało aktywowane!";
      },
      error => {
        this.message = error["error"]["error"];
      });

  }

}
