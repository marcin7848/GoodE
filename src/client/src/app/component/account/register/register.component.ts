import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../../../service/account/account.service";
import {first} from "rxjs/operators";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  message = "";

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService) { }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
      register_no: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required]
    });
  }

  get f() { return this.registerForm.controls; }

  onSubmit(){
    this.accountService.register(this.f.username.value, this.f.email.value, this.f.password.value, this.f.register_no.value,
      this.f.firstName.value, this.f.lastName.value)
    .pipe(first())
    .subscribe(
      data => {
        console.log("Rejestracja prawidlowa!!!");
      },
      error => {
        console.log(error);
        console.log("Niepoprawne dane rejestracji!");
        this.message = error["error"]["error"];
      });
  }

}
