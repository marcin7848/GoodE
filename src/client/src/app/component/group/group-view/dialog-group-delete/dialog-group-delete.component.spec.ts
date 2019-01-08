import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogGroupDeleteComponent } from './dialog-group-delete.component';

describe('DialogGroupDeleteComponent', () => {
  let component: DialogGroupDeleteComponent;
  let fixture: ComponentFixture<DialogGroupDeleteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogGroupDeleteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogGroupDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
