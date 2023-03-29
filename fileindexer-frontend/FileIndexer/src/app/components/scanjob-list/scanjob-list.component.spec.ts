import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScanjobListComponent } from './scanjob-list.component';

describe('ScanjobListComponent', () => {
  let component: ScanjobListComponent;
  let fixture: ComponentFixture<ScanjobListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ScanjobListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScanjobListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
