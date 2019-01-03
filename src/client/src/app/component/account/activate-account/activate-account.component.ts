import { Component, OnInit } from '@angular/core';
import {AccountService} from "../../../service/account/account.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs/operators";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent implements OnInit {

  activationCode = "";
  message = "";

  constructor(private accountService: AccountService,
              private route: ActivatedRoute,
              private translateService: TranslateService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.activationCode = params['activationCode'];

      this.accountService.activateAccount(this.activationCode)
      .pipe(first())
      .subscribe(
        data => {
          this.message = this.translateService.instant('accountActivated');
          setTimeout(function(){
            location.href = '/login';
          }, 3000);
        },
        error => {
          this.message = error["error"]["error"];
        });
    });
  }
}
