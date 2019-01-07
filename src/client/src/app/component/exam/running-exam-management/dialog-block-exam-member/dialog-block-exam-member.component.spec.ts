import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogBlockExamMemberComponent } from './dialog-block-exam-member.component';

describe('DialogBlockExamMemberComponent', () => {
  let component: DialogBlockExamMemberComponent;
  let fixture: ComponentFixture<DialogBlockExamMemberComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogBlockExamMemberComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogBlockExamMemberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
