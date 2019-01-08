import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";

export interface DialogDeleteExamMemberData {
  idGroup: number;
  name: string;
}

@Component({
  selector: 'app-dialog-group-delete',
  templateUrl: './dialog-group-delete.component.html',
  styleUrls: ['./dialog-group-delete.component.scss']
})
export class DialogGroupDeleteComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DialogDeleteExamMemberData>,
    @Inject(MAT_DIALOG_DATA) public data: DialogDeleteExamMemberData) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
  }
}
