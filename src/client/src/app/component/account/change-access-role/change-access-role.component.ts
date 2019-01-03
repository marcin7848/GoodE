import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs/operators";
import {AccessRole} from "../../../model/AccessRole";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-change-access-role',
  templateUrl: './change-access-role.component.html',
  styleUrls: ['./change-access-role.component.scss']
})
export class ChangeAccessRoleComponent implements OnInit {
  formG: FormGroup;
  message = "";
  accessRoles: AccessRole[];
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private route: ActivatedRoute,
              private translateService: TranslateService) { }

  ngOnInit() {
    this.formG = this.formBuilder.group({
      username: ['', Validators.required],
      accessRole: ['', Validators.required]
    });

    this.accountService.getAllAccessRole()
    .pipe(first())
    .subscribe(
      data => {
        this.accessRoles = data;
      },
      error => {
        location.href = '/';
      });

  }

  get f() { return this.formG.controls; }

  onSubmit(){
    this.submitted = true;

    if (this.formG.invalid) {
      return;
    }

    this.loading = true;

    this.accountService.changeAccessRole(this.f.username.value, this.f.accessRole.value)
    .pipe(first())
    .subscribe(
      data => {
        this.loading = false;
        this.message = this.translateService.instant('accessRoleChanged');
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
      });
  }
}
