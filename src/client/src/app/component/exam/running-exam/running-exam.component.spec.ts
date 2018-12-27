import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RunningExamComponent } from './running-exam.component';

describe('RunningExamComponent', () => {
  let component: RunningExamComponent;
  let fixture: ComponentFixture<RunningExamComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RunningExamComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RunningExamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
