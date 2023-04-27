import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalformComponent } from './modalform.component';

describe('ModalformComponent', () => {
  let component: ModalformComponent;
  let fixture: ComponentFixture<ModalformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalformComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
