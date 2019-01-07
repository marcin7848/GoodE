import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Account} from "../../../model/Account";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";
import {Group} from "../../../model/Group";
import {GroupService} from "../../../service/group/group.service";
import {CookieService} from "ngx-cookie-service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-add-new-group',
  templateUrl: './add-new-group.component.html',
  styleUrls: ['./add-new-group.component.scss']
})
export class AddNewGroupComponent implements OnInit {

  addNewGroupForm: FormGroup;
  message = "";
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private groupService: GroupService,
              private router: Router,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) {}


  ngOnInit() {
    this.addNewGroupForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      password: [''],
      possibleToJoin: [false, Validators.required],
      acceptance: [false, Validators.required],
      hidden: [false, Validators.required]
    });
  }

  get f() { return this.addNewGroupForm.controls; }

  onSubmit(){
    this.submitted = true;

    if (this.addNewGroupForm.invalid) {
      return;
    }

    this.loading = true;

    this.groupService.addNewGroup(this.f.name.value, this.f.description.value,
      this.f.password.value, this.f.possibleToJoin.value, this.f.acceptance.value,
      this.f.hidden.value, null)
    .pipe(first())
    .subscribe(
      data => {
        this.message = this.translateService.instant('groupAdded');
        setTimeout(goToGroup, 2000, this.router);

        function goToGroup(router: Router){
          router.navigate(['/group']);
        }

      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }
}
