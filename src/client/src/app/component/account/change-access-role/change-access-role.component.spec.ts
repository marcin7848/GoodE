import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeAccessRoleComponent } from './change-access-role.component';

describe('ChangeAccessRoleComponent', () => {
  let component: ChangeAccessRoleComponent;
  let fixture: ComponentFixture<ChangeAccessRoleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeAccessRoleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeAccessRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
