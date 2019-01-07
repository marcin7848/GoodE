import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";

export interface DialogBlockExamMemberData {
  idExamMember: number;
  causeOfBlockade: string;
}

@Component({
  selector: 'app-dialog-block-exam-member',
  templateUrl: './dialog-block-exam-member.component.html',
  styleUrls: ['./dialog-block-exam-member.component.scss']
})
export class DialogBlockExamMemberComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DialogBlockExamMemberComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogBlockExamMemberData) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
  }

}
