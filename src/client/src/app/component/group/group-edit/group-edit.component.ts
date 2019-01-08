import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {GroupService} from "../../../service/group/group.service";
import {first} from "rxjs/operators";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {ActivatedRoute, Router} from "@angular/router";
import {Group} from "../../../model/Group";
import {TranslateService} from "@ngx-translate/core";
import {MatSnackBar} from "@angular/material";

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.scss']
})
export class GroupEditComponent implements OnInit {

  editGroupForm: FormGroup;
  message = "";
  loggedAccount: Account;
  id: number;
  group: Group;
  loading = false;
  submitted = false;

  constructor(private formBuilder: FormBuilder,
              private groupService: GroupService,
              private accountService: AccountService,
              private router: Router,
              private route: ActivatedRoute,
              private translateService: TranslateService,
              private snackBar: MatSnackBar) { }


  ngOnInit() {

    this.accountService.getLoggedAccount().
    subscribe(data => {
        this.loggedAccount = data;
        if(this.loggedAccount.accessRole.role != "ROLE_ADMIN"){
          this.router.navigate(['/']);
        }
      },
      error => {
        this.router.navigate(['/']);
      });


    this.route.params.subscribe(params => {
      this.id = params['id'];

      this.groupService.getGroup(this.id)
      .pipe(first())
      .subscribe(
        data => {
          this.group = data;
          this.f.name.setValue(this.group.name);
          this.f.description.setValue(this.group.description);
          this.f.possibleToJoin.setValue(this.group.possibleToJoin);
          this.f.acceptance.setValue(this.group.acceptance);
          this.f.hidden.setValue(this.group.hidden);
        },
        error => {
          this.router.navigate(['/']);
        });
    });

    this.editGroupForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      password: [''],
      possibleToJoin: [false, Validators.required],
      acceptance: [false, Validators.required],
      hidden: [false, Validators.required]
    });
  }

  get f() { return this.editGroupForm.controls; }

  onSubmit(idGroup: number){
    this.submitted = true;

    if (this.editGroupForm.invalid) {
      return;
    }

    this.loading = true;

    this.groupService.editGroup(idGroup, this.f.name.value, this.f.description.value,
      this.f.password.value, this.f.possibleToJoin.value, this.f.acceptance.value,
      this.f.hidden.value, null)
    .pipe(first())
    .subscribe(
      data => {
        this.router.navigate(['/group/'+this.id+'/view']);
      },
      error => {
        this.loading = false;
        this.message = error["error"]["error"];
        this.snackBar.open(this.message, this.translateService.instant('close'), {
          duration: 5000,
        });
      });
  }

  cancel(){
    this.router.navigate(['/group/'+this.group.id+'/view']);
  }
}
