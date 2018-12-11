import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {GroupService} from "../../service/group/group.service";

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class GroupComponent implements OnInit {

  constructor(private router: Router, private groupService: GroupService) { }

  ngOnInit() {

  }

}
