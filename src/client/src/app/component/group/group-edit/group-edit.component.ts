import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {GroupService} from "../../../service/group/group.service";
import {first} from "rxjs/operators";
import {AccountService} from "../../../service/account/account.service";
import {Account} from "../../../model/Account";
import {ActivatedRoute, Router} from "@angular/router";
import {Group} from "../../../model/Group";

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

  constructor(private formBuilder: FormBuilder,
              private groupService: GroupService,
              private accountService: AccountService,
              private router: Router,
              private route: ActivatedRoute) { }


  ngOnInit() {

    this.accountService.getLoggedAccount().
    subscribe(data => {
        this.loggedAccount = data;
        if(this.loggedAccount.accessRole.role != "ROLE_ADMIN"){
          this.router.navigate(['/']);
        }
      },
      error => {
        console.log("Nie mozna pobrac!");
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
          console.log("Nie mozna pobrac!");
          this.message = error["error"]["error"];
        });
    });

    this.editGroupForm = this.formBuilder.group({
      name: ['', Validators.required],
      description: [''],
      password: [''],
      possibleToJoin: ['', Validators.required],
      acceptance: ['', Validators.required],
      hidden: ['', Validators.required]
    });
  }

  get f() { return this.editGroupForm.controls; }

  onSubmit(idGroup: number){
    this.groupService.editGroup(idGroup, this.f.name.value, this.f.description.value,
      this.f.password.value, this.f.possibleToJoin.value, this.f.acceptance.value,
      this.f.hidden.value, null)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Grupa wyedyowana!");
        this.router.navigate(['/group/'+this.id+'/view']);
      },
      error => {
        console.log(error);
        console.log("Niepoprawne dane!");
        this.message = error["error"]["error"];
      });
  }
}
