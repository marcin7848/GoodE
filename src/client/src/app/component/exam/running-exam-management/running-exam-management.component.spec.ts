import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RunningExamManagementComponent } from './running-exam-management.component';

describe('RunningExamManagementComponent', () => {
  let component: RunningExamManagementComponent;
  let fixture: ComponentFixture<RunningExamManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RunningExamManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RunningExamManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
