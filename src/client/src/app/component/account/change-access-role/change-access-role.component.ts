import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs/operators";
import {AccessRole} from "../../../model/AccessRole";

@Component({
  selector: 'app-change-access-role',
  templateUrl: './change-access-role.component.html',
  styleUrls: ['./change-access-role.component.scss']
})
export class ChangeAccessRoleComponent implements OnInit {
  formG: FormGroup;
  accessGranted = false;
  message = "";
  accessRoles: AccessRole[];

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.formG = this.formBuilder.group({
      username: ['', Validators.required],
      accessRole: ['', Validators.required]
    });

    this.accountService.getAllAccessRole()
    .pipe(first())
    .subscribe(
      data => {
        this.accessGranted = true;
        console.log(data);
        this.accessRoles = data;
      },
      error => {
        this.message = error["error"]["error"];
      });

  }

  get f() { return this.formG.controls; }

  onSubmit(){
    this.accountService.changeAccessRole(this.f.username.value, this.f.accessRole.value)
    .pipe(first())
    .subscribe(
      data => {
        this.message = "Access Rola zmieniona!";
      },
      error => {
        this.message = error["error"]["error"];
      });
  }
}
