import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScanjobAddComponent } from './scanjob-add.component';

describe('ScanjobAddComponent', () => {
  let component: ScanjobAddComponent;
  let fixture: ComponentFixture<ScanjobAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScanjobAddComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScanjobAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
